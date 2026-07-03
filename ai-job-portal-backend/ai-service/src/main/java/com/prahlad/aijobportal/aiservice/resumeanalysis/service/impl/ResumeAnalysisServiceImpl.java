package com.prahlad.aijobportal.aiservice.resumeanalysis.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.event.AiEventPublisher;
import com.prahlad.aijobportal.aiservice.event.dto.ATSCompletedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.ResumeAnalyzedEvent;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.ResumeAnalysisAiResult;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import com.prahlad.aijobportal.aiservice.resumeanalysis.mapper.ResumeAnalysisMapper;
import com.prahlad.aijobportal.aiservice.resumeanalysis.repository.ResumeAnalysisRepository;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeAnalysisService;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Implements resume analysis per DAY07_AI_SERVICE.md. Security rule
 * "Candidate can analyze only own resume" is enforced structurally:
 * this service is only ever invoked with the {@code candidateId}
 * resolved from the authenticated JWT principal (see
 * {@link com.prahlad.aijobportal.aiservice.resumeanalysis.controller.ResumeAnalysisController}) —
 * there is no request parameter through which a candidate could supply
 * a different candidateId.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private static final String PROMPT_TEMPLATE = """
            You are an expert technical recruiter and ATS (Applicant Tracking System) evaluator.
            Analyze the following resume text and return a JSON object with these exact fields:
            - atsScore: an integer from 0 to 100 representing ATS compatibility and overall quality
            - strengths: an array of short strings describing the resume's strongest points
            - weaknesses: an array of short strings describing the resume's weaknesses
            - missingSkills: an array of short strings naming skills commonly expected for this candidate's apparent target roles but missing from the resume
            - recommendations: an array of short, actionable strings for improving the resume

            Resume text:
            %s
            """;

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeAnalysisMapper resumeAnalysisMapper;
    private final AiStructuredResponseService aiStructuredResponseService;
    private final AiEventPublisher aiEventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisCacheConfig.RESUME_ANALYSIS_CACHE, key = "#candidateId")
    public ResumeAnalysisResponse analyze(UUID candidateId, AnalyzeResumeRequest request) {
        String resumeTextHash = sha256(request.resumeText());

        ResumeAnalysis existing = resumeAnalysisRepository
                .findTopByCandidateIdAndResumeTextHashOrderByCreatedAtDesc(candidateId, resumeTextHash)
                .orElse(null);

        if (existing != null) {
            log.info("Reusing existing resume analysis {} for candidateId={} (duplicate resume text)", existing.getId(), candidateId);
            return resumeAnalysisMapper.toResponse(existing);
        }

        ResumeAnalysisAiResult aiResult = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(request.resumeText()), ResumeAnalysisAiResult.class);

        int clampedScore = Math.max(0, Math.min(100, aiResult.atsScore()));

        ResumeAnalysis entity = ResumeAnalysis.builder()
                .candidateId(candidateId)
                .resumeUrl(request.resumeUrl())
                .resumeText(request.resumeText())
                .resumeTextHash(resumeTextHash)
                .atsScore(clampedScore)
                .strengths(resumeAnalysisMapper.toDelimited(aiResult.strengths()))
                .weaknesses(resumeAnalysisMapper.toDelimited(aiResult.weaknesses()))
                .missingSkills(resumeAnalysisMapper.toDelimited(aiResult.missingSkills()))
                .recommendations(resumeAnalysisMapper.toDelimited(aiResult.recommendations()))
                .build();

        entity = resumeAnalysisRepository.save(entity);
        log.info("Created resume analysis {} for candidateId={} atsScore={}", entity.getId(), candidateId, clampedScore);

        aiEventPublisher.publishResumeAnalyzed(new ResumeAnalyzedEvent(
                entity.getId(), candidateId, candidateId, clampedScore, Instant.now()));
        aiEventPublisher.publishAtsCompleted(new ATSCompletedEvent(
                entity.getId(), candidateId, clampedScore, Instant.now()));

        return resumeAnalysisMapper.toResponse(entity);
    }

    @Override
    @Cacheable(cacheNames = RedisCacheConfig.RESUME_ANALYSIS_CACHE, key = "#candidateId")
    public ResumeAnalysisResponse getLatestForCandidate(UUID candidateId) {
        ResumeAnalysis entity = resumeAnalysisRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis", "candidateId", candidateId));
        return resumeAnalysisMapper.toResponse(entity);
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new AiGenerationException("Unable to process resume text", ex);
        }
    }
}

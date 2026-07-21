package com.prahlad.aijobportal.aiservice.resumeanalysis.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.event.AiEventPublisher;
import com.prahlad.aijobportal.aiservice.event.dto.ATSCompletedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.ResumeAnalyzedEvent;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.gemini.UntrustedTextGuard;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.ResumeAnalysisAiResult;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import com.prahlad.aijobportal.aiservice.resumeanalysis.mapper.ResumeAnalysisMapper;
import com.prahlad.aijobportal.aiservice.resumeanalysis.repository.ResumeAnalysisRepository;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeAnalysisService;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeTextExtractionService;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Implements resume analysis per DAY07_AI_SERVICE.md, upgraded by
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements": the candidate supplies only a resume PDF URL -
 * {@link ResumeTextExtractionService} downloads and extracts its text
 * server-side (PDF Resume -&gt; PDF Text Extraction -&gt; Gemini Analysis
 * -&gt; Structured Response) rather than requiring pre-extracted text
 * from the frontend. Security rule "Candidate can analyze only own
 * resume" is enforced structurally: this service is only ever invoked
 * with the {@code candidateId} resolved from the authenticated JWT
 * principal (see
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
            - professionalSummary: a short 2-3 sentence professional summary of the candidate, written from the resume content (if the resume already has a summary/objective section, refine it; otherwise synthesize one from the rest of the resume)
            - projects: an array of short strings, one per project found in the resume, naming the project and its key technology/outcome
            - certifications: an array of short strings naming certifications found in the resume; empty array if none are present
            - languages: an array of short strings naming spoken/written languages found in the resume; empty array if none are present
            - achievements: an array of short strings naming notable achievements, awards, or measurable accomplishments found in the resume; empty array if none are present

            Only extract projects, certifications, languages, and achievements that are actually present in the resume text - never invent them.

            %s

            Resume text:
            %s
            """;

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeAnalysisMapper resumeAnalysisMapper;
    private final AiStructuredResponseService aiStructuredResponseService;
    private final AiEventPublisher aiEventPublisher;
    private final ResumeTextExtractionService resumeTextExtractionService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConfig.RESUME_ANALYSIS_CACHE, key = "#candidateId"),
            @CacheEvict(cacheNames = RedisCacheConfig.INTERVIEW_PREP_TOPICS_CACHE, key = "#candidateId")
    })
    public ResumeAnalysisResponse analyze(UUID candidateId, AnalyzeResumeRequest request) {
        String resumeText = resumeTextExtractionService.extractText(request.resumeUrl());
        String resumeTextHash = sha256(resumeText);

        ResumeAnalysis existing = resumeAnalysisRepository
                .findTopByCandidateIdAndResumeTextHashOrderByCreatedAtDesc(candidateId, resumeTextHash)
                .orElse(null);

        if (existing != null) {
            log.info("Reusing existing resume analysis {} for candidateId={} (duplicate resume text)", existing.getId(), candidateId);
            return resumeAnalysisMapper.toResponse(existing);
        }

        ResumeAnalysisAiResult aiResult = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("RESUME", resumeText)),
                ResumeAnalysisAiResult.class);

        int clampedScore = Math.max(0, Math.min(100, aiResult.atsScore()));

        ResumeAnalysis entity = ResumeAnalysis.builder()
                .candidateId(candidateId)
                .resumeUrl(request.resumeUrl())
                .resumeText(resumeText)
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

        return withExtraction(resumeAnalysisMapper.toResponse(entity), aiResult);
    }

    /**
     * Layers the AI-only extraction fields (professional summary,
     * projects, certifications, languages, achievements) onto a
     * response otherwise built by {@link ResumeAnalysisMapper} from
     * the persisted entity. These fields are never written to the
     * entity, so this is the only place they are populated - a fresh
     * Gemini call just happened and {@code aiResult} still has them in
     * memory.
     */
    private ResumeAnalysisResponse withExtraction(ResumeAnalysisResponse base, ResumeAnalysisAiResult aiResult) {
        return new ResumeAnalysisResponse(
                base.id(),
                base.candidateId(),
                base.resumeUrl(),
                base.atsScore(),
                base.strengths(),
                base.weaknesses(),
                base.missingSkills(),
                base.recommendations(),
                base.createdAt(),
                aiResult.professionalSummary(),
                aiResult.projects() == null ? List.of() : aiResult.projects(),
                aiResult.certifications() == null ? List.of() : aiResult.certifications(),
                aiResult.languages() == null ? List.of() : aiResult.languages(),
                aiResult.achievements() == null ? List.of() : aiResult.achievements());
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

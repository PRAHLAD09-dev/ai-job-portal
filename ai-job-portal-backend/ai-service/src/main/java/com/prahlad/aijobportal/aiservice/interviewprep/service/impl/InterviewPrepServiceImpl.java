package com.prahlad.aijobportal.aiservice.interviewprep.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.gemini.UntrustedTextGuard;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.DetectedTopicsAiResult;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.InterviewPrepAiResult;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.request.GenerateInterviewPrepRequest;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.DetectedTopicsResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.InterviewPrepQuestionSetResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.entity.InterviewPrepQuestionSet;
import com.prahlad.aijobportal.aiservice.interviewprep.mapper.InterviewPrepMapper;
import com.prahlad.aijobportal.aiservice.interviewprep.repository.InterviewPrepQuestionSetRepository;
import com.prahlad.aijobportal.aiservice.interviewprep.service.InterviewPrepService;
import com.prahlad.aijobportal.aiservice.interviewprep.util.InterviewPrepPdfGenerator;
import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import com.prahlad.aijobportal.aiservice.resumeanalysis.repository.ResumeAnalysisRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implements the AI Interview Generator PRD's candidate-facing flow:
 * <pre>
 * Resume Upload -&gt; (existing) Resume Analysis -&gt; AI Interview Generator
 * </pre>
 * Deliberately reuses {@link ResumeAnalysisRepository} rather than
 * {@code ResumeTextExtractionService}: per the PRD's Step 1 ("Do NOT
 * ask the user to upload again... Reuse the existing resume extraction
 * pipeline"), the candidate's latest resume text is already sitting in
 * the {@code resume_analysis} table from their last "Analyze Resume"
 * run - re-downloading and re-extracting the PDF here would be
 * redundant work and an unnecessary network call.
 *
 * <p>This does mean the candidate must have run resume analysis at
 * least once before using this feature. That already matches the
 * product flow (Interview Generator sits after Resume Analysis in the
 * AI module), so it isn't a new constraint - but it's worth calling out
 * for anyone integrating the frontend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewPrepServiceImpl implements InterviewPrepService {

    private static final String TOPICS_PROMPT_TEMPLATE = """
            You are analyzing a candidate's resume to build a list of topics they
            can practice interview questions on. Return a JSON object with two
            fields:
            - skills: an array of short strings naming the specific skills and
              technologies mentioned in the resume (e.g. programming languages,
              frameworks, databases, tools). Do not invent skills not present in
              the resume text.
            - projects: an array of short strings naming each distinct project
              found in the resume, using the project's own name/title as written.

            %s

            Resume text:
            %s
            """;

    private static final String QUESTIONS_PROMPT_TEMPLATE = """
            You are a senior technical interviewer at a top product-based
            company, generating practice questions for a candidate from their
            own resume. Generate exactly %d interview questions, distributed
            across the topics listed below, covering only the requested
            question type and difficulty. Return a JSON object with a single
            field "questions": an array of objects, each with "topic" (must be
            exactly one of the topics listed below) and "question" (the
            question text only).

            Rules:
            - Questions only. Never include answers, explanations, or hints.
            - No duplicate or near-duplicate questions.
            - Base every question on the resume content actually provided.
            - For project topics, ask about architecture, technical decisions,
              and implementation details, with realistic follow-up depth.
            - Make questions realistic for a product-based company interview.
            - Do not number the questions yourselves; the count and order are
              handled by the caller.

            %s

            Difficulty: %s
            Question type: %s (TECHNICAL = questions about the listed
            skills/technologies; PROJECT_BASED = questions about the listed
            projects; HR = behavioral questions related to the candidate's
            background, not tied to a specific listed topic - use the topic
            "HR" for these; MIXED = a realistic blend of all of the above)

            Topics to cover: %s

            Resume text:
            %s
            """;

    private final InterviewPrepQuestionSetRepository interviewPrepQuestionSetRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final InterviewPrepMapper interviewPrepMapper;
    private final AiStructuredResponseService aiStructuredResponseService;
    private final InterviewPrepPdfGenerator interviewPrepPdfGenerator;

    @Override
    @Cacheable(cacheNames = RedisCacheConfig.INTERVIEW_PREP_TOPICS_CACHE, key = "#candidateId")
    public DetectedTopicsResponse detectTopics(UUID candidateId) {
        ResumeAnalysis resumeAnalysis = latestResumeAnalysis(candidateId);

        DetectedTopicsAiResult aiResult = aiStructuredResponseService.generateStructured(
                TOPICS_PROMPT_TEMPLATE.formatted(
                        UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("RESUME", resumeAnalysis.getResumeText())),
                DetectedTopicsAiResult.class);

        return new DetectedTopicsResponse(
                aiResult.skills() == null ? List.of() : aiResult.skills(),
                aiResult.projects() == null ? List.of() : aiResult.projects());
    }

    @Override
    @Transactional
    public InterviewPrepQuestionSetResponse generate(UUID candidateId, GenerateInterviewPrepRequest request) {
        ResumeAnalysis resumeAnalysis = latestResumeAnalysis(candidateId);

        String topicsList = String.join(", ", request.selectedTopics());

        InterviewPrepAiResult aiResult = aiStructuredResponseService.generateStructured(
                QUESTIONS_PROMPT_TEMPLATE.formatted(
                        request.countOrDefault(),
                        UntrustedTextGuard.INSTRUCTION,
                        request.difficultyOrDefault(),
                        request.questionTypeOrDefault(),
                        UntrustedTextGuard.wrap("SELECTED_TOPICS", topicsList),
                        UntrustedTextGuard.wrap("RESUME", resumeAnalysis.getResumeText())),
                InterviewPrepAiResult.class);

        List<InterviewPrepAiResult.Item> items = aiResult.questions() == null ? List.of() : aiResult.questions();

        InterviewPrepQuestionSet entity = InterviewPrepQuestionSet.builder()
                .candidateId(candidateId)
                .resumeAnalysisId(resumeAnalysis.getId())
                .selectedTopics(interviewPrepMapper.toDelimited(request.selectedTopics()))
                .difficulty(request.difficultyOrDefault())
                .questionType(request.questionTypeOrDefault())
                .questionCount(items.size())
                .questionsJson(interviewPrepMapper.toQuestionsJson(items))
                .build();

        entity = interviewPrepQuestionSetRepository.save(entity);
        log.info("Generated {} interview prep questions for candidateId={}", items.size(), candidateId);

        return interviewPrepMapper.toResponse(entity);
    }

    @Override
    public InterviewPrepQuestionSetResponse getLatest(UUID candidateId) {
        InterviewPrepQuestionSet entity = interviewPrepQuestionSetRepository
                .findTopByCandidateIdOrderByCreatedAtDesc(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No interview practice questions have been generated yet", "candidateId", candidateId));
        return interviewPrepMapper.toResponse(entity);
    }

    @Override
    public byte[] generatePdf(UUID candidateId, UUID questionSetId) {
        InterviewPrepQuestionSet entity = interviewPrepQuestionSetRepository
                .findByIdAndCandidateId(questionSetId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question set", "id", questionSetId));

        ResumeAnalysis resumeAnalysis = resumeAnalysisRepository.findById(entity.getResumeAnalysisId()).orElse(null);
        String resumeFileName = resumeAnalysis == null ? null : fileNameFromUrl(resumeAnalysis.getResumeUrl());

        return interviewPrepPdfGenerator.generate(interviewPrepMapper.toResponse(entity), resumeFileName);
    }

    private ResumeAnalysis latestResumeAnalysis(UUID candidateId) {
        return resumeAnalysisRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Please analyze your resume first before generating interview questions",
                        "candidateId", candidateId));
    }

    private String fileNameFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        int lastSlash = url.lastIndexOf('/');
        return lastSlash == -1 ? url : url.substring(lastSlash + 1);
    }
}

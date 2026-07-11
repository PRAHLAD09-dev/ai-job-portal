package com.prahlad.aijobportal.aiservice.interview.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.exception.AiAccessDeniedException;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.external.RecruiterLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.gemini.UntrustedTextGuard;
import com.prahlad.aijobportal.aiservice.interview.dto.InterviewQuestionAiResult;
import com.prahlad.aijobportal.aiservice.interview.dto.request.GenerateInterviewQuestionsRequest;
import com.prahlad.aijobportal.aiservice.interview.dto.response.InterviewQuestionResponse;
import com.prahlad.aijobportal.aiservice.interview.entity.InterviewQuestion;
import com.prahlad.aijobportal.aiservice.interview.enums.QuestionCategory;
import com.prahlad.aijobportal.aiservice.interview.enums.QuestionDifficulty;
import com.prahlad.aijobportal.aiservice.interview.mapper.InterviewQuestionMapper;
import com.prahlad.aijobportal.aiservice.interview.repository.InterviewQuestionRepository;
import com.prahlad.aijobportal.aiservice.interview.service.InterviewQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewQuestionServiceImpl implements InterviewQuestionService {

    private static final String PROMPT_TEMPLATE = """
            You are an expert technical interviewer. Generate exactly %d interview
            questions for the job below. Mix difficulties and categories
            appropriately for the role's experience level. Return a JSON object
            with a single field "questions": an array of objects, each with
            "question" (the question text), "difficulty" (one of EASY, MEDIUM,
            HARD), and "category" (one of TECHNICAL, BEHAVIORAL, SITUATIONAL,
            SYSTEM_DESIGN, CULTURE_FIT).

            %s

            Job title: %s
            Experience level: %s
            Description:
            %s
            Required skills:
            %s
            """;

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewQuestionMapper interviewQuestionMapper;
    private final JobLookupService jobLookupService;
    private final RecruiterLookupService recruiterLookupService;
    private final AiStructuredResponseService aiStructuredResponseService;

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisCacheConfig.INTERVIEW_QUESTIONS_CACHE, key = "#request.jobId()")
    public List<InterviewQuestionResponse> generate(UUID recruiterUserId, String bearerToken,
            GenerateInterviewQuestionsRequest request) {
        RecruiterSummaryResponse recruiter = recruiterLookupService.fetchCurrentRecruiter(bearerToken);
        JobDetailSummaryResponse job = jobLookupService.fetchJob(request.jobId());

        if (!job.companyId().equals(recruiter.companyId())) {
            throw new AiAccessDeniedException(
                    "You may only generate interview questions for jobs belonging to your own company");
        }

        String skills = job.skills() == null ? ""
                : job.skills().stream()
                        .map(JobDetailSummaryResponse.JobSkillSummaryResponse::name)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");

        InterviewQuestionAiResult aiResult = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(request.countOrDefault(),
                        UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("JOB_TITLE", job.title()),
                        job.experienceLevel(),
                        UntrustedTextGuard.wrap("JOB_DESCRIPTION", job.description()),
                        UntrustedTextGuard.wrap("JOB_REQUIRED_SKILLS", skills)),
                InterviewQuestionAiResult.class);

        List<InterviewQuestion> entities = new ArrayList<>();

        for (InterviewQuestionAiResult.Item item : aiResult.questions()) {
            InterviewQuestion question = InterviewQuestion.builder()
                    .jobId(job.id())
                    .question(item.question())
                    .difficulty(parseDifficulty(item.difficulty()))
                    .category(parseCategory(item.category()))
                    .build();

            entities.add(question);
        }

        List<InterviewQuestion> saved = interviewQuestionRepository.saveAll(entities);
        log.info("Generated {} interview questions for jobId={}", saved.size(), job.id());

        return saved.stream().map(interviewQuestionMapper::toResponse).toList();
    }

    @Override
    @Cacheable(cacheNames = RedisCacheConfig.INTERVIEW_QUESTIONS_CACHE, key = "#jobId")
    public List<InterviewQuestionResponse> getForJob(UUID jobId) {
        return interviewQuestionRepository.findByJobId(jobId).stream()
                .map(interviewQuestionMapper::toResponse)
                .toList();
    }

    private QuestionDifficulty parseDifficulty(String raw) {
        try {
            return QuestionDifficulty.valueOf(raw.strip().toUpperCase());
        } catch (Exception ex) {
            throw new AiGenerationException("The AI provider returned an unrecognized question difficulty: " + raw);
        }
    }

    private QuestionCategory parseCategory(String raw) {
        try {
            return QuestionCategory.valueOf(raw.strip().toUpperCase());
        } catch (Exception ex) {
            throw new AiGenerationException("The AI provider returned an unrecognized question category: " + raw);
        }
    }
}

package com.prahlad.aijobportal.aiservice.interview.service.impl;

import com.prahlad.aijobportal.aiservice.exception.AiAccessDeniedException;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.external.RecruiterLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.interview.dto.InterviewQuestionAiResult;
import com.prahlad.aijobportal.aiservice.interview.dto.request.GenerateInterviewQuestionsRequest;
import com.prahlad.aijobportal.aiservice.interview.dto.response.InterviewQuestionResponse;
import com.prahlad.aijobportal.aiservice.interview.entity.InterviewQuestion;
import com.prahlad.aijobportal.aiservice.interview.mapper.InterviewQuestionMapper;
import com.prahlad.aijobportal.aiservice.interview.repository.InterviewQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewQuestionServiceImplTest {

    @Mock private InterviewQuestionRepository interviewQuestionRepository;
    @Mock private JobLookupService jobLookupService;
    @Mock private RecruiterLookupService recruiterLookupService;
    @Mock private AiStructuredResponseService aiStructuredResponseService;

    private InterviewQuestionMapper interviewQuestionMapper;
    private InterviewQuestionServiceImpl interviewQuestionService;

    private UUID recruiterUserId;
    private UUID jobId;
    private String bearerToken;

    @BeforeEach
    void setUp() {
        interviewQuestionMapper = new InterviewQuestionMapperImplForTest();
        interviewQuestionService = new InterviewQuestionServiceImpl(
                interviewQuestionRepository, interviewQuestionMapper, jobLookupService,
                recruiterLookupService, aiStructuredResponseService);

        recruiterUserId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        bearerToken = "Bearer test-token";
    }

    @Test
    void generate_throwsAccessDenied_whenJobBelongsToDifferentCompany() {
        UUID recruiterCompanyId = UUID.randomUUID();
        UUID jobCompanyId = UUID.randomUUID();

        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken))
                .thenReturn(new RecruiterSummaryResponse(UUID.randomUUID(), recruiterUserId, "r@x.com", "Recruiter", recruiterCompanyId, "MyCo"));
        when(jobLookupService.fetchJob(jobId))
                .thenReturn(new JobDetailSummaryResponse(jobId, jobCompanyId, "OtherCo", "Backend Engineer",
                        "desc", "FULL_TIME", "MID", "REMOTE", "PUBLISHED", null, null, null, null, Instant.now(), List.of(), List.of(), List.of()));

        GenerateInterviewQuestionsRequest request = new GenerateInterviewQuestionsRequest(jobId, 5);

        assertThatThrownBy(() -> interviewQuestionService.generate(recruiterUserId, bearerToken, request))
                .isInstanceOf(AiAccessDeniedException.class);
    }

    @Test
    void generate_persistsGeneratedQuestions_whenJobBelongsToOwnCompany() {
        UUID companyId = UUID.randomUUID();

        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken))
                .thenReturn(new RecruiterSummaryResponse(UUID.randomUUID(), recruiterUserId, "r@x.com", "Recruiter", companyId, "MyCo"));
        when(jobLookupService.fetchJob(jobId))
                .thenReturn(new JobDetailSummaryResponse(jobId, companyId, "MyCo", "Backend Engineer",
                        "desc", "FULL_TIME", "MID", "REMOTE", "PUBLISHED", null, null, null, null, Instant.now(), List.of(), List.of(), List.of()));

        InterviewQuestionAiResult aiResult = new InterviewQuestionAiResult(List.of(
                new InterviewQuestionAiResult.Item("Explain the SOLID principles.", "MEDIUM", "TECHNICAL")));
        when(aiStructuredResponseService.generateStructured(anyString(), eq(InterviewQuestionAiResult.class)))
                .thenReturn(aiResult);

        when(interviewQuestionRepository.saveAll(any())).thenAnswer(invocation -> {
            List<InterviewQuestion> entities = invocation.getArgument(0);
            entities.forEach(e -> e.setId(UUID.randomUUID()));
            return entities;
        });

        GenerateInterviewQuestionsRequest request = new GenerateInterviewQuestionsRequest(jobId, 1);
        List<InterviewQuestionResponse> result = interviewQuestionService.generate(recruiterUserId, bearerToken, request);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).difficulty()).isEqualTo("MEDIUM");
        assertThat(result.get(0).category()).isEqualTo("TECHNICAL");
    }

    @Test
    void generate_throwsAiGenerationException_whenAiReturnsUnrecognizedDifficulty() {
        UUID companyId = UUID.randomUUID();

        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken))
                .thenReturn(new RecruiterSummaryResponse(UUID.randomUUID(), recruiterUserId, "r@x.com", "Recruiter", companyId, "MyCo"));
        when(jobLookupService.fetchJob(jobId))
                .thenReturn(new JobDetailSummaryResponse(jobId, companyId, "MyCo", "Backend Engineer",
                        "desc", "FULL_TIME", "MID", "REMOTE", "PUBLISHED", null, null, null, null, Instant.now(), List.of(), List.of(), List.of()));

        InterviewQuestionAiResult aiResult = new InterviewQuestionAiResult(List.of(
                new InterviewQuestionAiResult.Item("Explain X.", "IMPOSSIBLE", "TECHNICAL")));
        when(aiStructuredResponseService.generateStructured(anyString(), eq(InterviewQuestionAiResult.class)))
                .thenReturn(aiResult);

        GenerateInterviewQuestionsRequest request = new GenerateInterviewQuestionsRequest(jobId, 1);

        assertThatThrownBy(() -> interviewQuestionService.generate(recruiterUserId, bearerToken, request))
                .isInstanceOf(AiGenerationException.class);
    }

    private static class InterviewQuestionMapperImplForTest implements InterviewQuestionMapper {
        @Override
        public InterviewQuestionResponse toResponse(InterviewQuestion entity) {
            if (entity == null) {
                return null;
            }
            return new InterviewQuestionResponse(
                    entity.getId(), entity.getJobId(), entity.getQuestion(),
                    entity.getDifficulty() == null ? null : entity.getDifficulty().name(),
                    entity.getCategory() == null ? null : entity.getCategory().name());
        }
    }
}

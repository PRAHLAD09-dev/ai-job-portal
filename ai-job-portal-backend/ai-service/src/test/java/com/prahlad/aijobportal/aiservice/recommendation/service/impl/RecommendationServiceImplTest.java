package com.prahlad.aijobportal.aiservice.recommendation.service.impl;

import com.prahlad.aijobportal.aiservice.event.AiEventPublisher;
import com.prahlad.aijobportal.aiservice.exception.AiAccessDeniedException;
import com.prahlad.aijobportal.aiservice.external.ApplicationLookupService;
import com.prahlad.aijobportal.aiservice.external.CandidateLookupService;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.external.RecruiterLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.JobRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.repository.JobRecommendationRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock private JobRecommendationRepository jobRecommendationRepository;
    @Mock private CandidateLookupService candidateLookupService;
    @Mock private JobLookupService jobLookupService;
    @Mock private RecruiterLookupService recruiterLookupService;
    @Mock private ApplicationLookupService applicationLookupService;
    @Mock private AiStructuredResponseService aiStructuredResponseService;
    @Mock private AiEventPublisher aiEventPublisher;

    private RecommendationServiceImpl recommendationService;

    private UUID recruiterUserId;
    private UUID jobId;
    private String bearerToken;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(
                jobRecommendationRepository, candidateLookupService, jobLookupService,
                recruiterLookupService, applicationLookupService, aiStructuredResponseService, aiEventPublisher);

        recruiterUserId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        bearerToken = "Bearer test-token";
    }

    @Test
    void recommendCandidates_throwsAccessDenied_whenJobBelongsToDifferentCompany() {
        UUID recruiterCompanyId = UUID.randomUUID();
        UUID jobCompanyId = UUID.randomUUID();

        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken))
                .thenReturn(new RecruiterSummaryResponse(UUID.randomUUID(), recruiterUserId, "r@x.com", "Recruiter", recruiterCompanyId, "MyCo"));
        when(jobLookupService.fetchJob(jobId))
                .thenReturn(new JobDetailSummaryResponse(jobId, jobCompanyId, "OtherCo", "Backend Engineer",
                        "desc", "FULL_TIME", "MID", "REMOTE", "PUBLISHED", Instant.now(), List.of()));

        assertThatThrownBy(() -> recommendationService.recommendCandidates(recruiterUserId, bearerToken, jobId))
                .isInstanceOf(AiAccessDeniedException.class);

        verify(applicationLookupService, never()).fetchApplicationsForJob(anyString(), any());
    }

    @Test
    void recommendJobs_returnsEmptyList_whenNoOpenJobsExist() {
        UUID candidateId = UUID.randomUUID();
        when(candidateLookupService.fetchCurrentCandidate(bearerToken))
                .thenReturn(new CandidateProfileSummaryResponse(
                        candidateId, candidateId, "c@x.com", "Candidate", "Backend Dev", "Summary",
                        "City", "State", "Country", List.of(), List.of(), List.of()));
        when(jobLookupService.fetchLatestJobs()).thenReturn(List.of());

        List<JobRecommendationResponse> result = recommendationService.recommendJobs(candidateId, bearerToken);

        assertThat(result).isEmpty();
        verify(aiStructuredResponseService, never()).generateStructured(anyString(), any());
    }
}

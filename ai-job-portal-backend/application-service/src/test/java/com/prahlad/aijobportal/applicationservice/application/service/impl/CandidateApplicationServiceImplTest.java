package com.prahlad.aijobportal.applicationservice.application.service.impl;

import com.prahlad.aijobportal.applicationservice.application.dto.request.CreateApplicationRequest;
import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.exception.DuplicateApplicationException;
import com.prahlad.aijobportal.applicationservice.application.exception.JobClosedException;
import com.prahlad.aijobportal.applicationservice.application.exception.ResumeNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.mapper.ApplicationMapper;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import com.prahlad.aijobportal.applicationservice.application.service.ApplicationService;
import com.prahlad.aijobportal.applicationservice.application.service.CandidateLookupService;
import com.prahlad.aijobportal.applicationservice.application.service.JobLookupService;
import com.prahlad.aijobportal.applicationservice.event.ApplicationEventPublisher;
import com.prahlad.aijobportal.applicationservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.applicationservice.feign.dto.JobSummaryResponse;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateApplicationServiceImplTest {

    @Mock private JobApplicationRepository applicationRepository;
    @Mock private ApplicationMapper applicationMapper;
    @Mock private ApplicationService applicationService;
    @Mock private ApplicationTimelineService applicationTimelineService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;
    @Mock private JobLookupService jobLookupService;
    @Mock private CandidateLookupService candidateLookupService;

    @InjectMocks
    private CandidateApplicationServiceImpl candidateApplicationService;

    private UUID candidateUserId;
    private UUID candidateId;
    private UUID jobId;
    private UUID companyId;
    private String bearerToken;
    private JobSummaryResponse job;
    private CandidateProfileSummaryResponse candidate;

    @BeforeEach
    void setUp() {
        candidateUserId = UUID.randomUUID();
        candidateId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        bearerToken = "Bearer test-token";

        job = new JobSummaryResponse(jobId, companyId, "Acme Inc", "Backend Engineer", "PUBLISHED", null);
        candidate = new CandidateProfileSummaryResponse(candidateId, candidateUserId, "jane@example.com", "Jane Doe",
                List.of(new CandidateProfileSummaryResponse.ResumeSummaryResponse(UUID.randomUUID(), "resume.pdf", "https://cdn.example.com/resume.pdf")));

        lenient().when(applicationRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void apply_createsApplication_whenJobOpenAndNotDuplicate() {
        when(jobLookupService.fetchJob(jobId)).thenReturn(job);
        when(candidateLookupService.fetchCurrentCandidate(bearerToken)).thenReturn(candidate);
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)).thenReturn(false);

        CreateApplicationRequest request = new CreateApplicationRequest(jobId, null, "I would love to join");

        candidateApplicationService.apply(candidateUserId, bearerToken, request);

        verify_savedApplicationHasExpectedFields();
    }

    @Test
    void apply_throws_whenJobNotPublished() {
        JobSummaryResponse closedJob = new JobSummaryResponse(jobId, companyId, "Acme Inc", "Backend Engineer", "DRAFT", null);
        when(jobLookupService.fetchJob(jobId)).thenReturn(closedJob);

        CreateApplicationRequest request = new CreateApplicationRequest(jobId, null, null);

        assertThatThrownBy(() -> candidateApplicationService.apply(candidateUserId, bearerToken, request))
                .isInstanceOf(JobClosedException.class);
    }

    @Test
    void apply_throws_whenDeadlinePassed() {
        JobSummaryResponse expiredJob = new JobSummaryResponse(jobId, companyId, "Acme Inc", "Backend Engineer",
                "PUBLISHED", Instant.now().minusSeconds(3600));
        when(jobLookupService.fetchJob(jobId)).thenReturn(expiredJob);

        CreateApplicationRequest request = new CreateApplicationRequest(jobId, null, null);

        assertThatThrownBy(() -> candidateApplicationService.apply(candidateUserId, bearerToken, request))
                .isInstanceOf(JobClosedException.class);
    }

    @Test
    void apply_throws_whenAlreadyApplied() {
        when(jobLookupService.fetchJob(jobId)).thenReturn(job);
        when(candidateLookupService.fetchCurrentCandidate(bearerToken)).thenReturn(candidate);
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)).thenReturn(true);

        CreateApplicationRequest request = new CreateApplicationRequest(jobId, null, null);

        assertThatThrownBy(() -> candidateApplicationService.apply(candidateUserId, bearerToken, request))
                .isInstanceOf(DuplicateApplicationException.class);
    }

    @Test
    void apply_throws_whenNoResumeOnProfile() {
        CandidateProfileSummaryResponse candidateWithNoResume = new CandidateProfileSummaryResponse(
                candidateId, candidateUserId, "jane@example.com", "Jane Doe", List.of());

        when(jobLookupService.fetchJob(jobId)).thenReturn(job);
        when(candidateLookupService.fetchCurrentCandidate(bearerToken)).thenReturn(candidateWithNoResume);

        CreateApplicationRequest request = new CreateApplicationRequest(jobId, null, null);

        assertThatThrownBy(() -> candidateApplicationService.apply(candidateUserId, bearerToken, request))
                .isInstanceOf(ResumeNotFoundException.class);
    }

    private void verify_savedApplicationHasExpectedFields() {
        org.mockito.ArgumentCaptor<JobApplication> captor = org.mockito.ArgumentCaptor.forClass(JobApplication.class);
        org.mockito.Mockito.verify(applicationRepository).save(captor.capture());

        JobApplication saved = captor.getValue();
        assertThat(saved.getCandidateId()).isEqualTo(candidateId);
        assertThat(saved.getCandidateUserId()).isEqualTo(candidateUserId);
        assertThat(saved.getJobId()).isEqualTo(jobId);
        assertThat(saved.getCompanyId()).isEqualTo(companyId);
        assertThat(saved.getResumeUrl()).isEqualTo("https://cdn.example.com/resume.pdf");
    }
}

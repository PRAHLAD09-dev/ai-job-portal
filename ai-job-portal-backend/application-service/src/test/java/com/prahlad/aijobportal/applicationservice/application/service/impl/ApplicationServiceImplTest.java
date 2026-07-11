package com.prahlad.aijobportal.applicationservice.application.service.impl;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.exception.ApplicationNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.exception.InvalidApplicationStateException;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationStatusChangedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateShortlistedEvent;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock private JobApplicationRepository applicationRepository;
    @Mock private ApplicationTimelineService applicationTimelineService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private JobApplication application;
    private UUID applicationId;
    private UUID actorId;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();
        actorId = UUID.randomUUID();
        application = JobApplication.builder()
                .candidateId(UUID.randomUUID())
                .candidateUserId(UUID.randomUUID())
                .candidateName("Jane Doe")
                .candidateEmail("jane@example.com")
                .companyId(UUID.randomUUID())
                .companyName("Acme Inc")
                .jobId(UUID.randomUUID())
                .jobTitle("Backend Engineer")
                .resumeUrl("https://cdn.example.com/resume.pdf")
                .status(ApplicationStatus.APPLIED)
                .build();
        application.setId(applicationId);

        lenient().when(applicationRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void getEntityOrThrow_returnsApplication_whenFound() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        JobApplication result = applicationService.getEntityOrThrow(applicationId);

        assertThat(result).isEqualTo(application);
    }

    @Test
    void getEntityOrThrow_throws_whenNotFound() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getEntityOrThrow(applicationId))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    void transitionStatus_appliedToShortlisted_succeedsAndPublishesEvent() {
        JobApplication result = applicationService.transitionStatus(
                application, ApplicationStatus.SHORTLISTED, actorId, "Great candidate", null);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.SHORTLISTED);
        verify(applicationTimelineService).recordTransition(application, ApplicationStatus.APPLIED,
                ApplicationStatus.SHORTLISTED, actorId, "Great candidate");
        verify(applicationEventPublisher).publishEvent(any(ApplicationStatusChangedEvent.class));
        verify(applicationEventPublisher).publishEvent(any(CandidateShortlistedEvent.class));
    }

    @Test
    void transitionStatus_toInterview_setsInterviewDate() {
        java.time.Instant interviewDate = java.time.Instant.now().plusSeconds(86_400);
        application.setStatus(ApplicationStatus.SHORTLISTED);

        JobApplication result = applicationService.transitionStatus(
                application, ApplicationStatus.INTERVIEW, actorId, null, interviewDate);

        assertThat(result.getInterviewDate()).isEqualTo(interviewDate);
    }

    @Test
    void transitionStatus_hiringRejectedCandidate_throws() {
        application.setStatus(ApplicationStatus.REJECTED);

        assertThatThrownBy(() -> applicationService.transitionStatus(
                application, ApplicationStatus.HIRED, actorId, null, null))
                .isInstanceOf(InvalidApplicationStateException.class)
                .hasMessageContaining("Cannot hire a rejected candidate");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void transitionStatus_rejectingHiredCandidate_throws() {
        application.setStatus(ApplicationStatus.HIRED);

        assertThatThrownBy(() -> applicationService.transitionStatus(
                application, ApplicationStatus.REJECTED, actorId, null, null))
                .isInstanceOf(InvalidApplicationStateException.class)
                .hasMessageContaining("Cannot reject a hired candidate");
    }

    @Test
    void transitionStatus_onTerminalApplication_throws() {
        application.setStatus(ApplicationStatus.WITHDRAWN);

        assertThatThrownBy(() -> applicationService.transitionStatus(
                application, ApplicationStatus.UNDER_REVIEW, actorId, null, null))
                .isInstanceOf(InvalidApplicationStateException.class)
                .hasMessageContaining("already reached a completed state");
    }

    @Test
    void transitionStatus_illegalJump_throws() {
        assertThatThrownBy(() -> applicationService.transitionStatus(
                application, ApplicationStatus.HIRED, actorId, null, null))
                .isInstanceOf(InvalidApplicationStateException.class);
    }

    @Test
    void transitionStatus_toWithdrawn_setsWithdrawnAt() {
        JobApplication result = applicationService.transitionStatus(
                application, ApplicationStatus.WITHDRAWN, actorId, "No longer interested", null);

        assertThat(result.getWithdrawnAt()).isNotNull();
    }
}

package com.prahlad.aijobportal.applicationservice.application.service.impl;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.exception.ApplicationNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.exception.InvalidApplicationStateException;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import com.prahlad.aijobportal.applicationservice.application.service.ApplicationService;
import com.prahlad.aijobportal.applicationservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationStatusChangedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateHiredEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateRejectedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateShortlistedEvent;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implements the core application status state machine. Per
 * DAY06_APPLICATION_SERVICE.md's Validation section: a completed
 * (terminal) application can never be updated again, a rejected
 * candidate can never be hired, and a hired candidate can never be
 * rejected.
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    /**
     * Legal forward transitions from each non-terminal status. WITHDRAWN
     * is reachable from every non-terminal status (candidate-initiated,
     * handled the same way as any other transition here) and REJECTED is
     * reachable from every non-terminal status (recruiter-initiated).
     */
    private static final Map<ApplicationStatus, Set<ApplicationStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ApplicationStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ApplicationStatus.APPLIED,
                EnumSet.of(ApplicationStatus.UNDER_REVIEW, ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.UNDER_REVIEW,
                EnumSet.of(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.SHORTLISTED,
                EnumSet.of(ApplicationStatus.INTERVIEW, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.INTERVIEW,
                EnumSet.of(ApplicationStatus.OFFERED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.OFFERED,
                EnumSet.of(ApplicationStatus.HIRED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
    }

    private final JobApplicationRepository applicationRepository;
    private final ApplicationTimelineService applicationTimelineService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public JobApplication getEntityOrThrow(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.RECRUITER_DASHBOARD_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CANDIDATE_DASHBOARD_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.APPLICATION_STATISTICS_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.RECENT_APPLICATIONS_CACHE, allEntries = true)
    })
    public JobApplication transitionStatus(@NotNull JobApplication application, @NotNull ApplicationStatus newStatus,
                                            UUID changedBy, String remarks, Instant interviewDate) {
        ApplicationStatus oldStatus = application.getStatus();
        validateTransition(oldStatus, newStatus);

        application.setStatus(newStatus);
        if (newStatus == ApplicationStatus.INTERVIEW) {
            application.setInterviewDate(interviewDate);
        }
        if (newStatus == ApplicationStatus.WITHDRAWN) {
            application.setWithdrawnAt(Instant.now());
        }
        if (remarks != null && !remarks.isBlank()) {
            application.setNotes(remarks);
        }

        JobApplication saved = applicationRepository.save(application);

        applicationTimelineService.recordTransition(saved, oldStatus, newStatus, changedBy, remarks);

        applicationEventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                saved.getId(), saved.getJobId(), saved.getCandidateId(), saved.getCandidateUserId(),
                saved.getCompanyId(), oldStatus, newStatus, changedBy, Instant.now()));

        publishTerminalEvent(saved, newStatus, remarks);

        return saved;
    }

    private void validateTransition(ApplicationStatus oldStatus, ApplicationStatus newStatus) {
        if (newStatus == ApplicationStatus.HIRED && oldStatus == ApplicationStatus.REJECTED) {
            throw new InvalidApplicationStateException("Cannot hire a rejected candidate");
        }
        if (newStatus == ApplicationStatus.REJECTED && oldStatus == ApplicationStatus.HIRED) {
            throw new InvalidApplicationStateException("Cannot reject a hired candidate");
        }
        if (oldStatus.isTerminal()) {
            throw new InvalidApplicationStateException(
                    "Cannot update an application that has already reached a completed state: " + oldStatus);
        }
        Set<ApplicationStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidApplicationStateException(
                    "Cannot transition application from " + oldStatus + " to " + newStatus);
        }
    }

    private void publishTerminalEvent(JobApplication application, ApplicationStatus newStatus, String remarks) {
        switch (newStatus) {
            case SHORTLISTED -> applicationEventPublisher.publishEvent(new CandidateShortlistedEvent(
                    application.getId(), application.getJobId(), application.getCandidateId(),
                    application.getCandidateUserId(), application.getJobTitle(), Instant.now()));
            case REJECTED -> applicationEventPublisher.publishEvent(new CandidateRejectedEvent(
                    application.getId(), application.getJobId(), application.getCandidateId(),
                    application.getCandidateUserId(), application.getJobTitle(), remarks, Instant.now()));
            case HIRED -> applicationEventPublisher.publishEvent(new CandidateHiredEvent(
                    application.getId(), application.getJobId(), application.getCandidateId(),
                    application.getCandidateUserId(), application.getCompanyId(), application.getJobTitle(), Instant.now()));
            default -> {
                // No dedicated event for this transition.
            }
        }
    }
}

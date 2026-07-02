package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Core application read/mutation logic shared by both the candidate-
 * and recruiter-facing service layers (per DAY06_APPLICATION_SERVICE.md's
 * Services section). Centralizes status-transition validation so the
 * business rules ("Cannot update completed application", "Cannot hire
 * rejected candidate", "Cannot reject hired candidate") are enforced in
 * exactly one place regardless of which side of the workflow triggers
 * the change.
 */
public interface ApplicationService {

    JobApplication getEntityOrThrow(UUID applicationId);

    /**
     * Transitions {@code application} to {@code newStatus}, validating
     * the transition is legal, persisting the new state, recording an
     * {@link com.prahlad.aijobportal.applicationservice.timeline.entity.ApplicationTimeline}
     * entry, and publishing the corresponding Kafka event(s).
     */
    JobApplication transitionStatus(JobApplication application, ApplicationStatus newStatus, UUID changedBy,
                                     String remarks, Instant interviewDate);
}

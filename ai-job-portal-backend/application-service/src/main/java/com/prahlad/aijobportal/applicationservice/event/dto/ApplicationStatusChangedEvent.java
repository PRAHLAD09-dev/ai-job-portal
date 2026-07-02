package com.prahlad.aijobportal.applicationservice.event.dto;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public record ApplicationStatusChangedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        UUID companyId,
        ApplicationStatus oldStatus,
        ApplicationStatus newStatus,
        UUID changedBy,
        Instant changedAt
) {
}

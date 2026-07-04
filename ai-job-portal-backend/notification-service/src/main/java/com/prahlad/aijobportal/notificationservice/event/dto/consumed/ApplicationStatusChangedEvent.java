package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Application Service ApplicationStatusChangedEvent (topic application-status-changed). */
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

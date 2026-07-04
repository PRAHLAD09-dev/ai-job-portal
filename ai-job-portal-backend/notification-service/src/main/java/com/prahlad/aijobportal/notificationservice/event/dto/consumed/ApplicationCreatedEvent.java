package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Application Service ApplicationCreatedEvent (topic application-created). */
public record ApplicationCreatedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        UUID companyId,
        String jobTitle,
        Instant appliedAt
) {
}

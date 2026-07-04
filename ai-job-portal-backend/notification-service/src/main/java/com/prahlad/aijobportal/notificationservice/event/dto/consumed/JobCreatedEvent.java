package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Job Service JobCreatedEvent (topic job-created). */
public record JobCreatedEvent(
        UUID jobId,
        UUID companyId,
        UUID recruiterUserId,
        String title,
        Instant createdAt
) {
}

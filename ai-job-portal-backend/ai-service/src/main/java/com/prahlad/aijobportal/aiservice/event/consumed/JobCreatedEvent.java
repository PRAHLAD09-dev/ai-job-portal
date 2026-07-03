package com.prahlad.aijobportal.aiservice.event.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Job Service's {@code JobCreatedEvent} (topic {@code job-created}). */
public record JobCreatedEvent(
        UUID jobId,
        UUID companyId,
        UUID recruiterUserId,
        String title,
        Instant createdAt
) {
}

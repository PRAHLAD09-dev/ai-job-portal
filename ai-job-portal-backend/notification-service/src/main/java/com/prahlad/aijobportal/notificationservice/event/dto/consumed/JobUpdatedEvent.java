package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Job Service JobUpdatedEvent (topic job-updated). Note: no recruiterUserId is published on update, only companyId. */
public record JobUpdatedEvent(
        UUID jobId,
        UUID companyId,
        String title,
        Instant updatedAt
) {
}

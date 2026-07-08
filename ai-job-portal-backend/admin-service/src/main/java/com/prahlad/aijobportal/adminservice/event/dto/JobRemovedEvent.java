package com.prahlad.aijobportal.adminservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an administrator removes (archives) a job, per
 * DAY09_ADMIN_SERVICE.md's Kafka section.
 */
public record JobRemovedEvent(
        UUID jobId,
        UUID companyId,
        String jobTitle,
        UUID removedByAdminId,
        Instant removedAt
) {
}

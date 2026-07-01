package com.prahlad.aijobportal.jobservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record JobCreatedEvent(
        UUID jobId,
        UUID companyId,
        UUID recruiterUserId,
        String title,
        Instant createdAt
) {
}

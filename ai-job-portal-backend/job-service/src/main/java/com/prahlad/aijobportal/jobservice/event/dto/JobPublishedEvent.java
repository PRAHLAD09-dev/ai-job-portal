package com.prahlad.aijobportal.jobservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record JobPublishedEvent(
        UUID jobId,
        UUID companyId,
        String title,
        Instant publishedAt
) {
}

package com.prahlad.aijobportal.jobservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record JobClosedEvent(
        UUID jobId,
        UUID companyId,
        String title,
        Instant closedAt
) {
}

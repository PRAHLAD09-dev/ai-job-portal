package com.prahlad.aijobportal.jobservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record JobDeletedEvent(
        UUID jobId,
        UUID companyId,
        Instant deletedAt
) {
}

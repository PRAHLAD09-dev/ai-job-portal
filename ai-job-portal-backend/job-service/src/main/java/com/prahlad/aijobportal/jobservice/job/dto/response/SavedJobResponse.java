package com.prahlad.aijobportal.jobservice.job.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SavedJobResponse(
        UUID id,
        JobSummaryResponse job,
        Instant savedAt
) {
}

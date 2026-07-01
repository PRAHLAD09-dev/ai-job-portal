package com.prahlad.aijobportal.jobservice.job.dto.response;

import java.util.UUID;

public record JobStatisticsResponse(
        UUID companyId,
        long totalJobs,
        long activeJobs,
        long closedJobs,
        long draftJobs
) {
}

package com.prahlad.aijobportal.jobservice.job.dto.response;

import java.util.UUID;

/**
 * DAY11 "Saved Job Statistics": how many candidates have bookmarked a
 * given job, for the {@code GET /jobs/me/saved-statistics} endpoint
 * that backs Recruiter Service's dashboard aggregation.
 */
public record JobSavedCountResponse(
        UUID jobId,
        String jobTitle,
        long savedCount
) {
}

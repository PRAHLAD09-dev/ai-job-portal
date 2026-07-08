package com.prahlad.aijobportal.jobservice.admin.dto.response;

/**
 * Platform-wide job counts, consumed by Admin Service's Dashboard and Job
 * Management features (Total Jobs / Platform Job Statistics), per
 * DAY09_ADMIN_SERVICE.md.
 */
public record JobPlatformStatisticsResponse(
        long totalJobs,
        long draftJobs,
        long publishedJobs,
        long closedJobs,
        long archivedJobs,
        long featuredJobs
) {
}

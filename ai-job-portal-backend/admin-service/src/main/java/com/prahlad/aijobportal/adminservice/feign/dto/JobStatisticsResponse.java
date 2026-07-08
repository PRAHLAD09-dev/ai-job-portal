package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of Job Service's
 * {@code JobPlatformStatisticsResponse} DTO, as returned by
 * {@code GET /api/v1/jobs/internal/admin/jobs/statistics}.
 */
public record JobStatisticsResponse(
        long totalJobs,
        long draftJobs,
        long publishedJobs,
        long closedJobs,
        long archivedJobs,
        long featuredJobs
) {
}

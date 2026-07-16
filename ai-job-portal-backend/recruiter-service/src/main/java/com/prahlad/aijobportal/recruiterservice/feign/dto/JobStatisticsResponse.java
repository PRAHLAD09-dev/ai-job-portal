package com.prahlad.aijobportal.recruiterservice.feign.dto;

import java.util.UUID;

/**
 * Mirrors the shape of Job Service's {@code JobStatisticsResponse}, as
 * returned (wrapped in {@code ApiResponse}) by {@code GET /jobs/me/statistics}.
 * Kept as its own DTO since microservices must not share compiled DTOs
 * across module boundaries.
 */
public record JobStatisticsResponse(
        UUID companyId,
        long totalJobs,
        long activeJobs,
        long closedJobs,
        long draftJobs
) {
}

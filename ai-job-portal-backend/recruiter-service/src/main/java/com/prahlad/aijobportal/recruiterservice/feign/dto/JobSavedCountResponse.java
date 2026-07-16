package com.prahlad.aijobportal.recruiterservice.feign.dto;

import java.util.UUID;

/**
 * Mirrors the shape of Job Service's {@code JobSavedCountResponse}, as
 * returned (wrapped in {@code ApiResponse}) by {@code GET /jobs/me/saved-statistics}.
 * Kept as its own DTO since microservices must not share compiled DTOs
 * across module boundaries.
 */
public record JobSavedCountResponse(
        UUID jobId,
        String jobTitle,
        long savedCount
) {
}

package com.prahlad.aijobportal.recruiterservice.feign.dto;

import java.util.Map;
import java.util.UUID;

/**
 * Mirrors the shape of Application Service's {@code ApplicationStatisticsResponse},
 * as returned (wrapped in {@code ApiResponse}) by
 * {@code GET /recruiter/applications/statistics}. Kept as its own DTO
 * since microservices must not share compiled DTOs across module
 * boundaries.
 */
public record ApplicationStatisticsResponse(
        UUID companyId,
        long totalApplications,
        Map<String, Long> countByStatus
) {
}

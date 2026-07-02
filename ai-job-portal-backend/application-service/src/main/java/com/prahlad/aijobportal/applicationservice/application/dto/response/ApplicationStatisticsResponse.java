package com.prahlad.aijobportal.applicationservice.application.dto.response;

import java.util.Map;
import java.util.UUID;

/**
 * Recruiter dashboard aggregate view over a company's applications,
 * per DAY06's "Application Statistics" DTO and "Recruiter Dashboard"
 * Redis cache entry.
 */
public record ApplicationStatisticsResponse(
        UUID companyId,
        long totalApplications,
        Map<String, Long> countByStatus
) {
}

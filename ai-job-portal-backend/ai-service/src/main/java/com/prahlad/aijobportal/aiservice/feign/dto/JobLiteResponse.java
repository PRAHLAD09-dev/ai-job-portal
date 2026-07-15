package com.prahlad.aijobportal.aiservice.feign.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Job Service's {@code JobSummaryResponse}, used
 * as the candidate pool for AI-generated job recommendations.
 *
 * <p>Extended for DAY10 with salary fields (already present on Job
 * Service's real response) so the job-match prompt can reason about
 * Salary Match without an extra per-job detail call.
 */
public record JobLiteResponse(
        UUID id,
        String companyName,
        String title,
        String jobType,
        String experienceLevel,
        String workMode,
        List<String> cities,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        String currency,
        Instant publishedAt
) {
}

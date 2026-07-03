package com.prahlad.aijobportal.aiservice.feign.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Job Service's {@code JobSummaryResponse}, used
 * as the candidate pool for AI-generated job recommendations.
 */
public record JobLiteResponse(
        UUID id,
        String companyName,
        String title,
        String jobType,
        String experienceLevel,
        String workMode,
        List<String> cities,
        Instant publishedAt
) {
}

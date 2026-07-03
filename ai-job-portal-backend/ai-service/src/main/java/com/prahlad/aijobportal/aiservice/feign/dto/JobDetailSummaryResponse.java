package com.prahlad.aijobportal.aiservice.feign.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Job Service's {@code JobResponse}, as returned
 * by the PUBLIC {@code GET /api/v1/jobs/{jobId}} endpoint. Used as AI
 * prompt context for ATS scoring, skill gap analysis, interview
 * question generation, and cover letter generation.
 */
public record JobDetailSummaryResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String title,
        String description,
        String jobType,
        String experienceLevel,
        String workMode,
        String status,
        Instant applicationDeadline,
        List<JobSkillSummaryResponse> skills
) {

    public record JobSkillSummaryResponse(String name, String requiredProficiency, boolean mandatory) {
    }
}

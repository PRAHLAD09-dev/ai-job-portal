package com.prahlad.aijobportal.aiservice.feign.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Job Service's {@code JobResponse}, as returned
 * by the PUBLIC {@code GET /api/v1/jobs/{jobId}} endpoint. Used as AI
 * prompt context for ATS scoring, job/candidate matching, skill gap
 * analysis, interview question generation, and cover letter generation.
 *
 * <p>Extended for DAY10 to carry salary and location fields (already
 * present on Job Service's real response) so the AI job-match prompt
 * can reason about Salary Match and Location Match dimensions. Job
 * Service itself is untouched.
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
        BigDecimal minSalary,
        BigDecimal maxSalary,
        String salaryType,
        String currency,
        Instant applicationDeadline,
        List<JobSkillSummaryResponse> skills,
        List<JobLocationSummaryResponse> locations,
        List<JobRequirementSummaryResponse> requirements
) {

    public record JobSkillSummaryResponse(String name, String requiredProficiency, boolean mandatory) {
    }

    public record JobLocationSummaryResponse(String city, String state, String country) {
    }

    public record JobRequirementSummaryResponse(String type, String description) {
    }
}

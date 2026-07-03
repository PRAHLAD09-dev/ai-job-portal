package com.prahlad.aijobportal.aiservice.feign.dto;

import java.util.UUID;

/**
 * Minimal projection of Application Service's {@code ApplicationSummaryResponse},
 * used as the applicant pool for AI-generated candidate recommendations
 * (recruiter ranking applicants for a specific job).
 */
public record ApplicationSummaryResponse(
        UUID id,
        UUID candidateId,
        String candidateName,
        UUID jobId,
        String jobTitle,
        String status
) {
}

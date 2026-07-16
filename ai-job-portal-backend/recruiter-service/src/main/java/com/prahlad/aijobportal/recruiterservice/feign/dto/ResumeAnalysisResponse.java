package com.prahlad.aijobportal.recruiterservice.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors (a subset of) the shape of AI Service's {@code ResumeAnalysisResponse},
 * as returned (wrapped in {@code ApiResponse}) by
 * {@code GET /ai/internal/recruiter/resume-analysis/{candidateId}}. Only
 * the fields the Recruiter Dashboard's "AI Match" card needs are
 * mirrored here — kept as its own DTO since microservices must not
 * share compiled DTOs across module boundaries. {@code @JsonIgnoreProperties}
 * is required (unlike this service's other Feign DTOs, which mirror
 * their source response's fields exactly) because AI Service's real
 * response carries many more AI-output fields (strengths, weaknesses,
 * recommendations, etc.) that the dashboard doesn't use.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeAnalysisResponse(
        UUID id,
        UUID candidateId,
        int atsScore,
        Instant createdAt
) {
}

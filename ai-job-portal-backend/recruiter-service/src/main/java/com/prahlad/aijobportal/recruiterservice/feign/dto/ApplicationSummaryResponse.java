package com.prahlad.aijobportal.recruiterservice.feign.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors the shape of Application Service's {@code ApplicationSummaryResponse},
 * as returned (wrapped in {@code ApiResponse}/{@code PageResponse}) by
 * {@code GET /recruiter/applications}. Kept as its own DTO since
 * microservices must not share compiled DTOs across module boundaries.
 * {@code status} is kept as a raw String (not an enum) deliberately —
 * same reasoning as Application Service's own Job Service Feign DTOs:
 * this service should not need to change every time another service
 * adds a new status value.
 */
public record ApplicationSummaryResponse(
        UUID id,
        UUID candidateId,
        String candidateName,
        UUID jobId,
        String jobTitle,
        String companyName,
        String status,
        Instant appliedAt,
        Instant interviewDate,
        boolean viewed
) {
}

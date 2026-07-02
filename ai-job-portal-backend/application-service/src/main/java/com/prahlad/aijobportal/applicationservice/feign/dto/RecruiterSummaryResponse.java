package com.prahlad.aijobportal.applicationservice.feign.dto;

import java.util.UUID;

/**
 * Mirrors the shape of Recruiter Service's {@code RecruiterResponse}
 * DTO, as returned (wrapped in {@code ApiResponse}) by
 * {@code GET /api/v1/recruiter/profile}. Application Service uses this
 * purely to resolve the authenticated recruiter's
 * {@code companyId}/{@code id} for company-scoped authorization — it
 * never duplicates Recruiter Service's business logic.
 */
public record RecruiterSummaryResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        UUID companyId,
        String companyName
) {
}

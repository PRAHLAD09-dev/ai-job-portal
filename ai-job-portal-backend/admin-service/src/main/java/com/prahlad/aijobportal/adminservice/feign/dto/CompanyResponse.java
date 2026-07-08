package com.prahlad.aijobportal.adminservice.feign.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors the shape of Recruiter Service's {@code AdminCompanyResponse}
 * DTO, as returned (wrapped in {@code ApiResponse}) by
 * {@code GET /api/v1/companies/internal/admin/companies/**}.
 */
public record CompanyResponse(
        UUID id,
        String name,
        String slug,
        String industry,
        String companySize,
        String websiteUrl,
        String email,
        String logoUrl,
        String verificationStatus,
        int activeJobCount,
        int totalHires,
        Instant createdAt,
        Instant updatedAt
) {
}

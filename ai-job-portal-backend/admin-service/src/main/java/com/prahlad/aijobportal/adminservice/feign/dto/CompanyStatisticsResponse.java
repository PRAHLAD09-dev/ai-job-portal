package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of Recruiter Service's
 * {@code CompanyPlatformStatisticsResponse} DTO, as returned by
 * {@code GET /api/v1/companies/internal/admin/companies/statistics}.
 */
public record CompanyStatisticsResponse(
        long totalCompanies,
        long pendingCompanies,
        long verifiedCompanies,
        long rejectedCompanies,
        long suspendedCompanies
) {
}

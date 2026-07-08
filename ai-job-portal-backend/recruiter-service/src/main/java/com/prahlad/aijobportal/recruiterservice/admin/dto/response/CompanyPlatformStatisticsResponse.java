package com.prahlad.aijobportal.recruiterservice.admin.dto.response;

/**
 * Platform-wide company counts, consumed by Admin Service's Dashboard
 * feature (Total Companies), per DAY09_ADMIN_SERVICE.md.
 */
public record CompanyPlatformStatisticsResponse(
        long totalCompanies,
        long pendingCompanies,
        long verifiedCompanies,
        long rejectedCompanies,
        long suspendedCompanies
) {
}

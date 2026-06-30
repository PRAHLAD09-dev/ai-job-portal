package com.prahlad.aijobportal.recruiterservice.company.dto.response;

import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;

import java.util.UUID;

/**
 * Lightweight statistics summary for the recruiter dashboard, per
 * DAY04's "Recruiter Dashboard APIs" / "Company Statistics" features.
 */
public record CompanyStatisticsResponse(
        UUID companyId,
        int activeJobCount,
        int totalHires,
        int recruiterCount,
        VerificationStatus verificationStatus
) {
}

package com.prahlad.aijobportal.recruiterservice.admin.dto.response;

import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Company projection returned by Recruiter Service's internal admin
 * endpoints, consumed exclusively by Admin Service via Feign. Kept
 * separate from the recruiter-facing {@code CompanyResponse} record
 * since Admin Service does not need location/social-link detail for its
 * moderation list/detail views.
 */
public record AdminCompanyResponse(
        UUID id,
        String name,
        String slug,
        Industry industry,
        CompanySize companySize,
        String websiteUrl,
        String email,
        String logoUrl,
        VerificationStatus verificationStatus,
        int activeJobCount,
        int totalHires,
        Instant createdAt,
        Instant updatedAt
) {
}

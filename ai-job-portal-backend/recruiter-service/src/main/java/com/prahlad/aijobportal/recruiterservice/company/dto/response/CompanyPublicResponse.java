package com.prahlad.aijobportal.recruiterservice.company.dto.response;

import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;

import java.util.List;
import java.util.UUID;

/**
 * Public-facing company profile, exposed via the unauthenticated
 * {@code GET /companies/{slug}/public} endpoint (per DAY04's "Company
 * Public Profile" feature). Deliberately excludes internal management
 * fields such as {@code email}, {@code phoneNumber}, {@code activeJobCount},
 * and {@code totalHires} that are only meaningful to the company's own
 * recruiters.
 */
public record CompanyPublicResponse(
        UUID id,
        String name,
        String slug,
        String description,
        Industry industry,
        CompanySize companySize,
        Integer foundedYear,
        String websiteUrl,
        String logoUrl,
        String bannerUrl,
        VerificationStatus verificationStatus,
        List<CompanyLocationResponse> locations,
        List<CompanySocialLinkResponse> socialLinks
) {
}

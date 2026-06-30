package com.prahlad.aijobportal.recruiterservice.company.dto.response;

import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;

import java.util.List;
import java.util.UUID;

/**
 * Full company profile, returned to the owning/member recruiter. Includes
 * internal management fields (verification status, active job count,
 * total hires) that are NOT exposed on the public profile.
 */
public record CompanyResponse(
        UUID id,
        String name,
        String slug,
        String description,
        Industry industry,
        CompanySize companySize,
        Integer foundedYear,
        String websiteUrl,
        String email,
        String phoneNumber,
        String logoUrl,
        String bannerUrl,
        VerificationStatus verificationStatus,
        int activeJobCount,
        int totalHires,
        List<CompanyLocationResponse> locations,
        List<CompanySocialLinkResponse> socialLinks
) {
}

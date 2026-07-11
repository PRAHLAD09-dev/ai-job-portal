package com.prahlad.aijobportal.recruiterservice.company.service;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.exception.RecruiterAccessDeniedException;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.RecruiterLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Centralizes the "a recruiter may only manage their own company"
 * business rule (PROJECT_SPECIFICATION.md Section 16), so every
 * company-scoped feature (locations, social links, assets) enforces the
 * same ownership check exactly once.
 */
@Service
@RequiredArgsConstructor
public class CompanyAccessGuard {

    private final RecruiterLookupService recruiterLookupService;

    /**
     * Resolves the authenticated user's recruiter profile and returns
     * their company — but only if they actually belong to it AND are its
     * owner. Every company-mutating operation (update, delete, asset
     * upload/delete, location/social-link management) goes through this
     * guard; today the only way a Recruiter row is created is via
     * createCompany() with owner=true, so this check changes nothing
     * about current behavior. It exists defensively for when a
     * non-owner "team member" recruiter (already anticipated by the
     * Recruiter entity's owner flag and one-company-to-many-recruiters
     * mapping) is introduced.
     */
    public Company resolveOwnedCompany(UUID userId) {
        Recruiter recruiter = recruiterLookupService.getByUserIdOrThrow(userId);
        if (!recruiter.isOwner()) {
            throw new RecruiterAccessDeniedException("Only the company owner may perform this action");
        }
        return recruiter.getCompany();
    }

    public void assertBelongsToCompany(UUID userId, UUID companyId) {
        Recruiter recruiter = recruiterLookupService.getByUserIdOrThrow(userId);
        if (!recruiter.getCompany().getId().equals(companyId)) {
            throw new RecruiterAccessDeniedException("You do not have permission to manage this company");
        }
    }
}

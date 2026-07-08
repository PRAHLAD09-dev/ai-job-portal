package com.prahlad.aijobportal.adminservice.companymanagement.service;

import com.prahlad.aijobportal.adminservice.feign.dto.CompanyResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.PageResponse;

import java.util.UUID;

/**
 * Backs Admin Service's Company Management feature
 * (DAY09_ADMIN_SERVICE.md). A thin orchestration layer over
 * {@code RecruiterServiceClient}; never reimplements Recruiter
 * Service's business logic.
 */
public interface CompanyManagementService {

    PageResponse<CompanyResponse> searchCompanies(String keyword, String status, int page, int size);

    CompanyResponse getCompany(UUID companyId);

    CompanyResponse verifyCompany(UUID companyId, AuthenticatedUser admin, String ipAddress);

    CompanyResponse rejectCompany(UUID companyId, AuthenticatedUser admin, String ipAddress);

    CompanyResponse suspendCompany(UUID companyId, AuthenticatedUser admin, String ipAddress);
}

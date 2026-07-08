package com.prahlad.aijobportal.recruiterservice.admin.service;

import com.prahlad.aijobportal.recruiterservice.admin.dto.response.AdminCompanyResponse;
import com.prahlad.aijobportal.recruiterservice.admin.dto.response.CompanyPlatformStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Backs Recruiter Service's internal admin endpoints, called exclusively
 * by Admin Service via Feign + the shared internal-service token. Reuses
 * the existing {@code Company}/{@code CompanyRepository} — no new
 * persistence concept and no duplication of {@code CompanyService}'s own
 * business logic.
 */
public interface AdminCompanyService {

    Page<AdminCompanyResponse> searchCompanies(String keyword, VerificationStatus status, Pageable pageable);

    AdminCompanyResponse getCompany(UUID companyId);

    AdminCompanyResponse verifyCompany(UUID companyId);

    AdminCompanyResponse rejectCompany(UUID companyId);

    AdminCompanyResponse suspendCompany(UUID companyId);

    CompanyPlatformStatisticsResponse getPlatformStatistics();
}

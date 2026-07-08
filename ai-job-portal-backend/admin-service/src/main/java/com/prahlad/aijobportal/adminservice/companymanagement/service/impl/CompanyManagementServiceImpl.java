package com.prahlad.aijobportal.adminservice.companymanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.companymanagement.service.CompanyManagementService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.CompanyVerifiedEvent;
import com.prahlad.aijobportal.adminservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyManagementServiceImpl implements CompanyManagementService {

    private final RecruiterServiceClient recruiterServiceClient;
    private final AuditLogService auditLogService;
    private final AdminEventPublisher adminEventPublisher;

    @Override
    @CircuitBreaker(name = "recruiterService")
    @Retry(name = "recruiterService")
    public PageResponse<CompanyResponse> searchCompanies(String keyword, String status, int page, int size) {
        return recruiterServiceClient.searchCompanies(keyword, status, page, size).getData();
    }

    @Override
    @CircuitBreaker(name = "recruiterService")
    @Retry(name = "recruiterService")
    public CompanyResponse getCompany(UUID companyId) {
        return recruiterServiceClient.getCompany(companyId).getData();
    }

    @Override
    @CircuitBreaker(name = "recruiterService")
    public CompanyResponse verifyCompany(UUID companyId, AuthenticatedUser admin, String ipAddress) {
        CompanyResponse company = recruiterServiceClient.verifyCompany(companyId).getData();

        auditLogService.record(admin, AuditActionType.COMPANY_VERIFIED, AuditTargetType.COMPANY, companyId,
                "Verified company " + company.name(), ipAddress);

        adminEventPublisher.publishCompanyVerified(
                new CompanyVerifiedEvent(companyId, company.name(), admin.userId(), Instant.now()));

        return company;
    }

    @Override
    @CircuitBreaker(name = "recruiterService")
    public CompanyResponse rejectCompany(UUID companyId, AuthenticatedUser admin, String ipAddress) {
        CompanyResponse company = recruiterServiceClient.rejectCompany(companyId).getData();

        auditLogService.record(admin, AuditActionType.COMPANY_REJECTED, AuditTargetType.COMPANY, companyId,
                "Rejected company " + company.name(), ipAddress);

        return company;
    }

    @Override
    @CircuitBreaker(name = "recruiterService")
    public CompanyResponse suspendCompany(UUID companyId, AuthenticatedUser admin, String ipAddress) {
        CompanyResponse company = recruiterServiceClient.suspendCompany(companyId).getData();

        auditLogService.record(admin, AuditActionType.COMPANY_SUSPENDED, AuditTargetType.COMPANY, companyId,
                "Suspended company " + company.name(), ipAddress);

        return company;
    }
}

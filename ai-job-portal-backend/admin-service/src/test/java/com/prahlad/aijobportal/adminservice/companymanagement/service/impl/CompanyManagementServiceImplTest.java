package com.prahlad.aijobportal.adminservice.companymanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.CompanyVerifiedEvent;
import com.prahlad.aijobportal.adminservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyManagementServiceImplTest {

    @Mock private RecruiterServiceClient recruiterServiceClient;
    @Mock private AuditLogService auditLogService;
    @Mock private AdminEventPublisher adminEventPublisher;

    private CompanyManagementServiceImpl companyManagementService;

    private AuthenticatedUser admin;
    private UUID companyId;
    private CompanyResponse verifiedCompany;
    private CompanyResponse rejectedCompany;

    @BeforeEach
    void setUp() {
        companyManagementService = new CompanyManagementServiceImpl(recruiterServiceClient, auditLogService, adminEventPublisher);

        admin = new AuthenticatedUser(UUID.randomUUID(), "admin@example.com", Set.of("SUPER_ADMIN"));
        companyId = UUID.randomUUID();
        verifiedCompany = new CompanyResponse(companyId, "Acme Corp", "acme-corp", "INFORMATION_TECHNOLOGY",
                "SIZE_11_50", "https://acme.com", "hr@acme.com", null, "VERIFIED", 3, 1, Instant.now(), Instant.now());
        rejectedCompany = new CompanyResponse(companyId, "Acme Corp", "acme-corp", "INFORMATION_TECHNOLOGY",
                "SIZE_11_50", "https://acme.com", "hr@acme.com", null, "REJECTED", 0, 0, Instant.now(), Instant.now());
    }

    @Test
    void verifyCompany_recordsAuditLogAndPublishesEvent() {
        when(recruiterServiceClient.verifyCompany(companyId)).thenReturn(ApiResponse.success(verifiedCompany));

        CompanyResponse result = companyManagementService.verifyCompany(companyId, admin, "127.0.0.1");

        assertThat(result.verificationStatus()).isEqualTo("VERIFIED");
        verify(auditLogService).record(eq(admin), eq(AuditActionType.COMPANY_VERIFIED),
                eq(AuditTargetType.COMPANY), eq(companyId), any(String.class), eq("127.0.0.1"));
        verify(adminEventPublisher).publishCompanyVerified(any(CompanyVerifiedEvent.class));
    }

    @Test
    void rejectCompany_recordsAuditLogButDoesNotPublishVerifiedEvent() {
        when(recruiterServiceClient.rejectCompany(companyId)).thenReturn(ApiResponse.success(rejectedCompany));

        CompanyResponse result = companyManagementService.rejectCompany(companyId, admin, "127.0.0.1");

        assertThat(result.verificationStatus()).isEqualTo("REJECTED");
        verify(auditLogService).record(eq(admin), eq(AuditActionType.COMPANY_REJECTED),
                eq(AuditTargetType.COMPANY), eq(companyId), any(String.class), eq("127.0.0.1"));
        verify(adminEventPublisher, never()).publishCompanyVerified(any());
    }
}

package com.prahlad.aijobportal.adminservice.jobmanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.JobRemovedEvent;
import com.prahlad.aijobportal.adminservice.feign.JobServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.JobResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobManagementServiceImplTest {

    @Mock private JobServiceClient jobServiceClient;
    @Mock private AuditLogService auditLogService;
    @Mock private AdminEventPublisher adminEventPublisher;

    private JobManagementServiceImpl jobManagementService;

    private AuthenticatedUser admin;
    private UUID jobId;
    private UUID companyId;
    private JobResponse removedJob;
    private JobResponse featuredJob;

    @BeforeEach
    void setUp() {
        jobManagementService = new JobManagementServiceImpl(jobServiceClient, auditLogService, adminEventPublisher);

        admin = new AuthenticatedUser(UUID.randomUUID(), "admin@example.com", Set.of("ADMIN"));
        jobId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        removedJob = new JobResponse(jobId, companyId, "Acme Corp", "Senior Engineer", "senior-engineer",
                "FULL_TIME", "REMOTE", "ARCHIVED", false, 42, Instant.now(), Instant.now(), Instant.now());
        featuredJob = new JobResponse(jobId, companyId, "Acme Corp", "Senior Engineer", "senior-engineer",
                "FULL_TIME", "REMOTE", "PUBLISHED", true, 42, Instant.now(), null, Instant.now());
    }

    @Test
    void removeJob_recordsAuditLogAndPublishesEvent() {
        when(jobServiceClient.removeJob(jobId)).thenReturn(ApiResponse.success(removedJob));

        JobResponse result = jobManagementService.removeJob(jobId, admin, "127.0.0.1");

        assertThat(result.status()).isEqualTo("ARCHIVED");
        verify(auditLogService).record(eq(admin), eq(AuditActionType.JOB_REMOVED),
                eq(AuditTargetType.JOB), eq(jobId), any(String.class), eq("127.0.0.1"));
        verify(adminEventPublisher).publishJobRemoved(any(JobRemovedEvent.class));
    }

    @Test
    void featureJob_recordsAuditLog() {
        when(jobServiceClient.featureJob(jobId)).thenReturn(ApiResponse.success(featuredJob));

        JobResponse result = jobManagementService.featureJob(jobId, admin, "127.0.0.1");

        assertThat(result.featured()).isTrue();
        verify(auditLogService).record(eq(admin), eq(AuditActionType.JOB_FEATURED),
                eq(AuditTargetType.JOB), eq(jobId), any(String.class), eq("127.0.0.1"));
    }
}

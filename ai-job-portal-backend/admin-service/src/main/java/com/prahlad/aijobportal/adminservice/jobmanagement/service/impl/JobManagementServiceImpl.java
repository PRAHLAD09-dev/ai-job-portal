package com.prahlad.aijobportal.adminservice.jobmanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.JobRemovedEvent;
import com.prahlad.aijobportal.adminservice.feign.JobServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.JobResponse;
import com.prahlad.aijobportal.adminservice.jobmanagement.service.JobManagementService;
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
public class JobManagementServiceImpl implements JobManagementService {

    private final JobServiceClient jobServiceClient;
    private final AuditLogService auditLogService;
    private final AdminEventPublisher adminEventPublisher;

    @Override
    @CircuitBreaker(name = "jobService")
    @Retry(name = "jobService")
    public PageResponse<JobResponse> searchJobs(String keyword, String status, UUID companyId, int page, int size) {
        return jobServiceClient.searchJobs(keyword, status, companyId, page, size).getData();
    }

    @Override
    @CircuitBreaker(name = "jobService")
    @Retry(name = "jobService")
    public JobResponse getJob(UUID jobId) {
        return jobServiceClient.getJob(jobId).getData();
    }

    @Override
    @CircuitBreaker(name = "jobService")
    public JobResponse removeJob(UUID jobId, AuthenticatedUser admin, String ipAddress) {
        JobResponse job = jobServiceClient.removeJob(jobId).getData();

        auditLogService.record(admin, AuditActionType.JOB_REMOVED, AuditTargetType.JOB, jobId,
                "Removed job " + job.title(), ipAddress);

        adminEventPublisher.publishJobRemoved(
                new JobRemovedEvent(jobId, job.companyId(), job.title(), admin.userId(), Instant.now()));

        return job;
    }

    @Override
    @CircuitBreaker(name = "jobService")
    public JobResponse restoreJob(UUID jobId, AuthenticatedUser admin, String ipAddress) {
        JobResponse job = jobServiceClient.restoreJob(jobId).getData();

        auditLogService.record(admin, AuditActionType.JOB_RESTORED, AuditTargetType.JOB, jobId,
                "Restored job " + job.title(), ipAddress);

        return job;
    }

    @Override
    @CircuitBreaker(name = "jobService")
    public JobResponse featureJob(UUID jobId, AuthenticatedUser admin, String ipAddress) {
        JobResponse job = jobServiceClient.featureJob(jobId).getData();

        auditLogService.record(admin, AuditActionType.JOB_FEATURED, AuditTargetType.JOB, jobId,
                "Featured job " + job.title(), ipAddress);

        return job;
    }

    @Override
    @CircuitBreaker(name = "jobService")
    public JobResponse unfeatureJob(UUID jobId, AuthenticatedUser admin, String ipAddress) {
        JobResponse job = jobServiceClient.unfeatureJob(jobId).getData();

        auditLogService.record(admin, AuditActionType.JOB_UNFEATURED, AuditTargetType.JOB, jobId,
                "Unfeatured job " + job.title(), ipAddress);

        return job;
    }
}

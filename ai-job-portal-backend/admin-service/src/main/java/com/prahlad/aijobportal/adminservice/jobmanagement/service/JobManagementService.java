package com.prahlad.aijobportal.adminservice.jobmanagement.service;

import com.prahlad.aijobportal.adminservice.feign.dto.JobResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.PageResponse;

import java.util.UUID;

/**
 * Backs Admin Service's Job Management feature (DAY09_ADMIN_SERVICE.md).
 * A thin orchestration layer over {@code JobServiceClient}; never
 * reimplements Job Service's business logic.
 */
public interface JobManagementService {

    PageResponse<JobResponse> searchJobs(String keyword, String status, UUID companyId, int page, int size);

    JobResponse getJob(UUID jobId);

    JobResponse removeJob(UUID jobId, AuthenticatedUser admin, String ipAddress);

    JobResponse restoreJob(UUID jobId, AuthenticatedUser admin, String ipAddress);

    JobResponse featureJob(UUID jobId, AuthenticatedUser admin, String ipAddress);

    JobResponse unfeatureJob(UUID jobId, AuthenticatedUser admin, String ipAddress);
}

package com.prahlad.aijobportal.jobservice.admin.service;

import com.prahlad.aijobportal.jobservice.admin.dto.response.AdminJobResponse;
import com.prahlad.aijobportal.jobservice.admin.dto.response.JobPlatformStatisticsResponse;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Backs Job Service's internal admin endpoints, called exclusively by
 * Admin Service via Feign + the shared internal-service token. Reuses
 * the existing {@code Job}/{@code JobRepository} — no new persistence
 * concept and no duplication of the recruiter-facing {@code JobService}'s
 * own business logic.
 */
public interface AdminJobService {

    Page<AdminJobResponse> searchJobs(String keyword, JobStatus status, UUID companyId, Pageable pageable);

    AdminJobResponse getJob(UUID jobId);

    AdminJobResponse removeJob(UUID jobId);

    AdminJobResponse restoreJob(UUID jobId);

    AdminJobResponse featureJob(UUID jobId);

    AdminJobResponse unfeatureJob(UUID jobId);

    JobPlatformStatisticsResponse getPlatformStatistics();
}

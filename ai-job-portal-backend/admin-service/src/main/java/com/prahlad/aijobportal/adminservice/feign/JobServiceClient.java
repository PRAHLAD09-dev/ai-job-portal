package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.JobResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.JobStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Synchronous Feign client to Job Service's internal-only admin
 * endpoints (never routed through the API Gateway). Authenticates via
 * {@link FeignClientConfig}'s internal-service-token interceptor. Used
 * exclusively to back Admin Service's Job Management and Dashboard
 * features (DAY09_ADMIN_SERVICE.md); never duplicates Job Service's own
 * business logic.
 */
@FeignClient(name = "JOB-SERVICE", path = CommonConstants.API_BASE_PATH + "/jobs/internal/admin", configuration = FeignClientConfig.class)
public interface JobServiceClient {

    @GetMapping("/jobs")
    ApiResponse<PageResponse<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID companyId,
            @RequestParam int page,
            @RequestParam int size);

    @GetMapping("/jobs/statistics")
    ApiResponse<JobStatisticsResponse> getStatistics();

    @GetMapping("/jobs/{jobId}")
    ApiResponse<JobResponse> getJob(@PathVariable("jobId") UUID jobId);

    @PatchMapping("/jobs/{jobId}/remove")
    ApiResponse<JobResponse> removeJob(@PathVariable("jobId") UUID jobId);

    @PatchMapping("/jobs/{jobId}/restore")
    ApiResponse<JobResponse> restoreJob(@PathVariable("jobId") UUID jobId);

    @PatchMapping("/jobs/{jobId}/feature")
    ApiResponse<JobResponse> featureJob(@PathVariable("jobId") UUID jobId);

    @PatchMapping("/jobs/{jobId}/unfeature")
    ApiResponse<JobResponse> unfeatureJob(@PathVariable("jobId") UUID jobId);
}

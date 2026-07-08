package com.prahlad.aijobportal.jobservice.admin.controller;

import com.prahlad.aijobportal.jobservice.admin.dto.response.AdminJobResponse;
import com.prahlad.aijobportal.jobservice.admin.dto.response.JobPlatformStatisticsResponse;
import com.prahlad.aijobportal.jobservice.admin.service.AdminJobService;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal-only, service-to-service endpoints backing Admin Service's Job
 * Management and Dashboard features (DAY09_ADMIN_SERVICE.md). Never
 * routed through the API Gateway and never callable with a normal user
 * bearer token — authenticated exclusively by
 * {@code InternalServiceAuthFilter} via the shared
 * {@code X-Internal-Service-Token} header. Reuses {@code AdminJobService}
 * (backed by the same {@code Job} entity/table) rather than duplicating
 * any Job Service business logic; existing recruiter/candidate-facing
 * {@code JobController} endpoints are untouched.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/jobs/internal/admin")
@RequiredArgsConstructor
@Tag(name = "Internal - Admin", description = "Service-to-service endpoints for Admin Service, not exposed through the API Gateway")
public class InternalAdminJobController {

    private final AdminJobService adminJobService;

    @GetMapping("/jobs")
    @Operation(summary = "List/search/filter jobs across every status (internal callers only)")
    public ResponseEntity<ApiResponse<PageResponse<AdminJobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) UUID companyId,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AdminJobResponse> page = adminJobService.searchJobs(keyword, status, companyId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/jobs/statistics")
    @Operation(summary = "Get platform-wide job statistics (internal callers only)")
    public ResponseEntity<ApiResponse<JobPlatformStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminJobService.getPlatformStatistics()));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "View a single job's admin profile (internal callers only)")
    public ResponseEntity<ApiResponse<AdminJobResponse>> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(adminJobService.getJob(jobId)));
    }

    @PatchMapping("/jobs/{jobId}/remove")
    @Operation(summary = "Remove (archive) a job (internal callers only)")
    public ResponseEntity<ApiResponse<AdminJobResponse>> removeJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success("Job removed successfully", adminJobService.removeJob(jobId)));
    }

    @PatchMapping("/jobs/{jobId}/restore")
    @Operation(summary = "Restore a previously removed job (internal callers only)")
    public ResponseEntity<ApiResponse<AdminJobResponse>> restoreJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success("Job restored successfully", adminJobService.restoreJob(jobId)));
    }

    @PatchMapping("/jobs/{jobId}/feature")
    @Operation(summary = "Mark a job as featured (internal callers only)")
    public ResponseEntity<ApiResponse<AdminJobResponse>> featureJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success("Job featured successfully", adminJobService.featureJob(jobId)));
    }

    @PatchMapping("/jobs/{jobId}/unfeature")
    @Operation(summary = "Remove a job's featured status (internal callers only)")
    public ResponseEntity<ApiResponse<AdminJobResponse>> unfeatureJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success("Job unfeatured successfully", adminJobService.unfeatureJob(jobId)));
    }
}

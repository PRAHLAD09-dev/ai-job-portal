package com.prahlad.aijobportal.adminservice.jobmanagement.controller;

import com.prahlad.aijobportal.adminservice.feign.dto.JobResponse;
import com.prahlad.aijobportal.adminservice.jobmanagement.service.JobManagementService;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin Service's Job Management feature (DAY09_ADMIN_SERVICE.md). Every
 * operation here is a thin wrapper over Job Service's internal admin
 * endpoints via {@code JobManagementService}.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/jobs")
@RequiredArgsConstructor
@Tag(name = "Admin - Job Management")
public class JobManagementController {

    private final JobManagementService jobManagementService;

    @GetMapping
    @Operation(summary = "List/search/filter jobs across every status")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(jobManagementService.searchJobs(keyword, status, companyId, page, size)));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "View a single job")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobManagementService.getJob(jobId)));
    }

    @PatchMapping("/{jobId}/remove")
    @Operation(summary = "Remove (archive) a job")
    public ResponseEntity<ApiResponse<JobResponse>> removeJob(@PathVariable UUID jobId,
                                                               @AuthenticationPrincipal AuthenticatedUser admin,
                                                               HttpServletRequest request) {
        JobResponse job = jobManagementService.removeJob(jobId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Job removed successfully", job));
    }

    @PatchMapping("/{jobId}/restore")
    @Operation(summary = "Restore a previously removed job")
    public ResponseEntity<ApiResponse<JobResponse>> restoreJob(@PathVariable UUID jobId,
                                                                @AuthenticationPrincipal AuthenticatedUser admin,
                                                                HttpServletRequest request) {
        JobResponse job = jobManagementService.restoreJob(jobId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Job restored successfully", job));
    }

    @PatchMapping("/{jobId}/feature")
    @Operation(summary = "Mark a job as featured")
    public ResponseEntity<ApiResponse<JobResponse>> featureJob(@PathVariable UUID jobId,
                                                                @AuthenticationPrincipal AuthenticatedUser admin,
                                                                HttpServletRequest request) {
        JobResponse job = jobManagementService.featureJob(jobId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Job featured successfully", job));
    }

    @PatchMapping("/{jobId}/unfeature")
    @Operation(summary = "Remove a job's featured status")
    public ResponseEntity<ApiResponse<JobResponse>> unfeatureJob(@PathVariable UUID jobId,
                                                                  @AuthenticationPrincipal AuthenticatedUser admin,
                                                                  HttpServletRequest request) {
        JobResponse job = jobManagementService.unfeatureJob(jobId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Job unfeatured successfully", job));
    }
}

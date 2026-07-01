package com.prahlad.aijobportal.jobservice.job.controller;

import com.prahlad.aijobportal.jobservice.job.dto.request.CreateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.request.JobSearchCriteria;
import com.prahlad.aijobportal.jobservice.job.dto.request.UpdateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobStatisticsResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import com.prahlad.aijobportal.jobservice.job.service.JobService;
import com.prahlad.aijobportal.jobservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Public job-browsing/search endpoints live under plain {@code /jobs/**}
 * (see {@code SecurityConfig.PUBLIC_GET_ENDPOINTS} for the exact
 * whitelist). Recruiter-scoped management endpoints live under
 * {@code /jobs/me/**}, which is intentionally excluded from that public
 * whitelist and therefore always requires authentication.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job browsing, search, and recruiter job management")
public class JobController {

    private final JobService jobService;

    // ---- Recruiter management (/jobs/me/**) ----

    @PostMapping("/me")
    @Operation(summary = "Create a new job (draft)")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody CreateJobRequest request) {
        JobResponse response = jobService.createJob(principal.userId(), bearerToken, request);
        return ResponseEntity.ok(ApiResponse.success("Job created successfully", response));
    }

    @PutMapping("/me/{jobId}")
    @Operation(summary = "Update an existing job")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId,
            @Valid @RequestBody UpdateJobRequest request) {
        JobResponse response = jobService.updateJob(principal.userId(), bearerToken, jobId, request);
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", response));
    }

    @DeleteMapping("/me/{jobId}")
    @Operation(summary = "Delete a job")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        jobService.deleteJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully", null));
    }

    @PostMapping("/me/{jobId}/publish")
    @Operation(summary = "Publish a draft or closed job")
    public ResponseEntity<ApiResponse<JobResponse>> publishJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        JobResponse response = jobService.publishJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job published successfully", response));
    }

    @PostMapping("/me/{jobId}/close")
    @Operation(summary = "Close a published job")
    public ResponseEntity<ApiResponse<JobResponse>> closeJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        JobResponse response = jobService.closeJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job closed successfully", response));
    }

    @PostMapping("/me/{jobId}/reopen")
    @Operation(summary = "Reopen a closed or archived job")
    public ResponseEntity<ApiResponse<JobResponse>> reopenJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        JobResponse response = jobService.reopenJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job reopened successfully", response));
    }

    @PostMapping("/me/{jobId}/duplicate")
    @Operation(summary = "Duplicate an existing job as a new draft")
    public ResponseEntity<ApiResponse<JobResponse>> duplicateJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        JobResponse response = jobService.duplicateJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job duplicated successfully", response));
    }

    @GetMapping("/me/{jobId}/preview")
    @Operation(summary = "Preview a job regardless of its current status")
    public ResponseEntity<ApiResponse<JobResponse>> previewJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        JobResponse response = jobService.previewJob(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @Operation(summary = "List jobs belonging to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<PageResponse<JobSummaryResponse>>> getMyCompanyJobs(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<JobSummaryResponse> response = jobService.getMyCompanyJobs(principal.userId(), bearerToken, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/statistics")
    @Operation(summary = "Get job statistics for the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<JobStatisticsResponse>> getMyCompanyStatistics(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        JobStatisticsResponse response = jobService.getMyCompanyStatistics(principal.userId(), bearerToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ---- Public browsing/search ----

    @GetMapping
    @Operation(summary = "Get all published jobs (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<JobSummaryResponse>>> getAllJobs(
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<JobSummaryResponse> response = jobService.searchJobs(emptyCriteria(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter published jobs with sorting and pagination")
    public ResponseEntity<ApiResponse<PageResponse<JobSummaryResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) Instant postedAfter,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        JobSearchCriteria criteria = new JobSearchCriteria(keyword, categoryId, skill, city, state, country,
                companyId, jobType, experienceLevel, workMode, minSalary, maxSalary, postedAfter);
        PageResponse<JobSummaryResponse> response = jobService.searchJobs(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the most recently published jobs")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> getLatestJobs() {
        return ResponseEntity.ok(ApiResponse.success(jobService.getLatestJobs()));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured jobs")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> getFeaturedJobs() {
        return ResponseEntity.ok(ApiResponse.success(jobService.getFeaturedJobs()));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending jobs (by view count)")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> getTrendingJobs() {
        return ResponseEntity.ok(ApiResponse.success(jobService.getTrendingJobs()));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get a job by its slug")
    public ResponseEntity<ApiResponse<JobResponse>> getJobBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobBySlug(slug)));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get a job by id")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(jobId)));
    }

    @GetMapping("/{jobId}/similar")
    @Operation(summary = "Get jobs similar to the given job")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> getSimilarJobs(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getSimilarJobs(jobId)));
    }

    private JobSearchCriteria emptyCriteria() {
        return new JobSearchCriteria(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}

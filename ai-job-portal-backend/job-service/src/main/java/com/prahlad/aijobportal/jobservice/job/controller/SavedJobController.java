package com.prahlad.aijobportal.jobservice.job.controller;

import com.prahlad.aijobportal.jobservice.job.dto.response.SavedJobResponse;
import com.prahlad.aijobportal.jobservice.job.service.SavedJobService;
import com.prahlad.aijobportal.jobservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Candidate-scoped "saved jobs" (bookmarks). Not in
 * {@code SecurityConfig.PUBLIC_GET_ENDPOINTS}, so every endpoint here
 * requires authentication by default.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/jobs/saved")
@RequiredArgsConstructor
@Tag(name = "Saved Jobs", description = "Candidate job bookmarks")
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    @Operation(summary = "Save (bookmark) a job")
    public ResponseEntity<ApiResponse<SavedJobResponse>> saveJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID jobId) {
        SavedJobResponse response = savedJobService.saveJob(principal.userId(), jobId);
        return ResponseEntity.ok(ApiResponse.success("Job saved successfully", response));
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "Unsave (remove bookmark from) a job")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID jobId) {
        savedJobService.unsaveJob(principal.userId(), jobId);
        return ResponseEntity.ok(ApiResponse.success("Job unsaved successfully", null));
    }

    @GetMapping
    @Operation(summary = "List the authenticated candidate's saved jobs")
    public ResponseEntity<ApiResponse<PageResponse<SavedJobResponse>>> getMySavedJobs(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<SavedJobResponse> response = savedJobService.getMySavedJobs(principal.userId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

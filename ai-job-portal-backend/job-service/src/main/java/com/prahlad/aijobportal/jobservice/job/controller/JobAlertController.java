package com.prahlad.aijobportal.jobservice.job.controller;

import com.prahlad.aijobportal.jobservice.job.dto.request.JobAlertRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobAlertResponse;
import com.prahlad.aijobportal.jobservice.job.service.JobAlertService;
import com.prahlad.aijobportal.jobservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Candidate-scoped saved job-alert criteria. Not in
 * {@code SecurityConfig.PUBLIC_GET_ENDPOINTS}, so every endpoint here
 * requires authentication by default.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/jobs/alerts")
@RequiredArgsConstructor
@Tag(name = "Job Alerts", description = "Candidate saved search alerts")
public class JobAlertController {

    private final JobAlertService jobAlertService;

    @PostMapping
    @Operation(summary = "Create a job alert")
    public ResponseEntity<ApiResponse<JobAlertResponse>> createAlert(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody JobAlertRequest request) {
        JobAlertResponse response = jobAlertService.createAlert(principal.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Job alert created successfully", response));
    }

    @PutMapping("/{alertId}")
    @Operation(summary = "Update a job alert")
    public ResponseEntity<ApiResponse<JobAlertResponse>> updateAlert(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID alertId,
            @Valid @RequestBody JobAlertRequest request) {
        JobAlertResponse response = jobAlertService.updateAlert(principal.userId(), alertId, request);
        return ResponseEntity.ok(ApiResponse.success("Job alert updated successfully", response));
    }

    @DeleteMapping("/{alertId}")
    @Operation(summary = "Delete a job alert")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID alertId) {
        jobAlertService.deleteAlert(principal.userId(), alertId);
        return ResponseEntity.ok(ApiResponse.success("Job alert deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "List the authenticated candidate's job alerts")
    public ResponseEntity<ApiResponse<List<JobAlertResponse>>> getMyAlerts(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.success(jobAlertService.getMyAlerts(principal.userId())));
    }
}

package com.prahlad.aijobportal.applicationservice.application.controller;

import com.prahlad.aijobportal.applicationservice.application.dto.request.CreateApplicationRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.applicationservice.application.service.CandidateApplicationService;
import com.prahlad.aijobportal.applicationservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.applicationservice.timeline.dto.response.TimelineResponse;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Candidate-facing application endpoints — apply, withdraw, track
 * status — per DAY06_APPLICATION_SERVICE.md's "Candidate" features
 * section. A candidate may only ever act on their own applications;
 * ownership is derived from the authenticated principal's user id, no
 * request parameter can override it.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/applications")
@RequiredArgsConstructor
@Tag(name = "Candidate Applications", description = "Apply for jobs, withdraw, and track application status")
public class CandidateApplicationController {

    private final CandidateApplicationService candidateApplicationService;
    private final ApplicationTimelineService applicationTimelineService;

    @PostMapping
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody CreateApplicationRequest request) {
        ApplicationResponse response = candidateApplicationService.apply(principal.userId(), bearerToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", response));
    }

    @PostMapping("/{applicationId}/withdraw")
    @Operation(summary = "Withdraw an application")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable java.util.UUID applicationId) {
        candidateApplicationService.withdraw(principal.userId(), applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "List the authenticated candidate's applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationSummaryResponse>>> getMyApplications(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        PageResponse<ApplicationSummaryResponse> response = candidateApplicationService.getMyApplications(principal.userId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/{applicationId}")
    @Operation(summary = "Get the authenticated candidate's application detail")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetail(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable java.util.UUID applicationId) {
        ApplicationResponse response = candidateApplicationService.getApplicationDetail(principal.userId(), applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/{applicationId}/timeline")
    @Operation(summary = "Get the status timeline for the authenticated candidate's application")
    public ResponseEntity<ApiResponse<List<TimelineResponse>>> getTimeline(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable java.util.UUID applicationId) {
        // Ownership is enforced by resolving the application through the
        // candidate-scoped service first; a not-found/forbidden response
        // is returned before the timeline is ever read.
        candidateApplicationService.getApplicationDetail(principal.userId(), applicationId);
        List<TimelineResponse> response = applicationTimelineService.getTimeline(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

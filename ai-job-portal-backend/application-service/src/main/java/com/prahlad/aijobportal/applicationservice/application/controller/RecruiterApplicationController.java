package com.prahlad.aijobportal.applicationservice.application.controller;

import com.prahlad.aijobportal.applicationservice.application.dto.request.ApplicationSearchCriteria;
import com.prahlad.aijobportal.applicationservice.application.dto.request.RecruiterNotesRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.request.UpdateApplicationStatusRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.service.RecruiterApplicationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Recruiter-facing application endpoints — view, filter, search,
 * shortlist, reject, schedule interview, offer, hire, and annotate —
 * per DAY06_APPLICATION_SERVICE.md's "Recruiter" features section. A
 * recruiter may only ever act on applications belonging to their own
 * company (enforced in the service layer via
 * {@link com.prahlad.aijobportal.applicationservice.application.service.ApplicationOwnershipGuard}).
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/recruiter/applications")
@RequiredArgsConstructor
@Tag(name = "Recruiter Applications", description = "Review, filter, and progress candidate applications through the hiring workflow")
public class RecruiterApplicationController {

    private final RecruiterApplicationService recruiterApplicationService;
    private final ApplicationTimelineService applicationTimelineService;

    @GetMapping
    @Operation(summary = "List/filter/search the authenticated recruiter's company applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationSummaryResponse>>> getApplications(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID jobId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) Instant appliedAfter,
            @RequestParam(required = false) Instant appliedBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ApplicationSearchCriteria criteria = new ApplicationSearchCriteria(keyword, jobId, status, appliedAfter, appliedBefore);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        PageResponse<ApplicationSummaryResponse> response =
                recruiterApplicationService.getApplications(principal.userId(), bearerToken, criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get a company application's detail")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetail(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        ApplicationResponse response = recruiterApplicationService.getApplicationDetail(principal.userId(), bearerToken, applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{applicationId}/timeline")
    @Operation(summary = "Get a company application's status timeline")
    public ResponseEntity<ApiResponse<List<TimelineResponse>>> getTimeline(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        recruiterApplicationService.getApplicationDetail(principal.userId(), bearerToken, applicationId);
        List<TimelineResponse> response = applicationTimelineService.getTimeline(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{applicationId}/status")
    @Operation(summary = "Update an application's status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponse response = recruiterApplicationService.updateStatus(principal.userId(), bearerToken, applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", response));
    }

    @PostMapping("/{applicationId}/review")
    @Operation(summary = "Move an application to UNDER_REVIEW")
    public ResponseEntity<ApiResponse<ApplicationResponse>> review(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.UNDER_REVIEW, null, null);
    }

    @PostMapping("/{applicationId}/shortlist")
    @Operation(summary = "Shortlist a candidate")
    public ResponseEntity<ApiResponse<ApplicationResponse>> shortlist(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.SHORTLISTED, null, null);
    }

    @PostMapping("/{applicationId}/interview")
    @Operation(summary = "Schedule an interview")
    public ResponseEntity<ApiResponse<ApplicationResponse>> scheduleInterview(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId,
            @RequestParam Instant interviewDate) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.INTERVIEW, interviewDate, null);
    }

    @PostMapping("/{applicationId}/offer")
    @Operation(summary = "Extend an offer to a candidate")
    public ResponseEntity<ApiResponse<ApplicationResponse>> offer(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.OFFERED, null, null);
    }

    @PostMapping("/{applicationId}/hire")
    @Operation(summary = "Hire a candidate")
    public ResponseEntity<ApiResponse<ApplicationResponse>> hire(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.HIRED, null, null);
    }

    @PostMapping("/{applicationId}/reject")
    @Operation(summary = "Reject a candidate")
    public ResponseEntity<ApiResponse<ApplicationResponse>> reject(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId,
            @RequestParam(required = false) String reason) {
        return status(principal, bearerToken, applicationId, ApplicationStatus.REJECTED, null, reason);
    }

    @PutMapping("/{applicationId}/notes")
    @Operation(summary = "Add or replace recruiter notes on an application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> addNotes(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID applicationId,
            @Valid @RequestBody RecruiterNotesRequest request) {
        ApplicationResponse response = recruiterApplicationService.addNotes(principal.userId(), bearerToken, applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Notes saved successfully", response));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get the authenticated recruiter's company application statistics")
    public ResponseEntity<ApiResponse<ApplicationStatisticsResponse>> getStatistics(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        ApplicationStatisticsResponse response = recruiterApplicationService.getStatistics(principal.userId(), bearerToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private ResponseEntity<ApiResponse<ApplicationResponse>> status(AuthenticatedUser principal, String bearerToken,
                                                                      UUID applicationId, ApplicationStatus status,
                                                                      Instant interviewDate, String remarks) {
        UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest(status, interviewDate, remarks);
        ApplicationResponse response = recruiterApplicationService.updateStatus(principal.userId(), bearerToken, applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", response));
    }
}

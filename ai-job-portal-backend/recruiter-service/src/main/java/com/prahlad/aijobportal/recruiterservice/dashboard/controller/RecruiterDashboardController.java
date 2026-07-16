package com.prahlad.aijobportal.recruiterservice.dashboard.controller;

import com.prahlad.aijobportal.recruiterservice.dashboard.dto.response.RecruiterDashboardResponse;
import com.prahlad.aijobportal.recruiterservice.dashboard.service.RecruiterDashboardService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DAY11 "Recruiter Dashboard Improvements". Extends recruiter-service
 * only (no new microservice); aggregates AI Match, Viewed Status, and
 * Saved Job Statistics live from AI Service, Application Service, and
 * Job Service via {@code RecruiterDashboardService}'s Feign clients.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/recruiter/dashboard")
@RequiredArgsConstructor
@Tag(name = "Recruiter Dashboard", description = "Aggregated AI Match, viewed status, and saved-job statistics for the authenticated recruiter's company")
public class RecruiterDashboardController {

    private final RecruiterDashboardService recruiterDashboardService;

    @GetMapping
    @Operation(summary = "Get the authenticated recruiter's dashboard",
            description = "Aggregates job statistics, application statistics, per-job saved (bookmark) counts, "
                    + "and the AI Match score + viewed status for the most recent applications - all sourced "
                    + "live via Feign from Job Service, Application Service, and AI Service; nothing is "
                    + "duplicated or persisted here.")
    public ResponseEntity<ApiResponse<RecruiterDashboardResponse>> getDashboard(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        return ResponseEntity.ok(ApiResponse.success(recruiterDashboardService.getDashboard(bearerToken)));
    }
}

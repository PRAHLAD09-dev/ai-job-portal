package com.prahlad.aijobportal.adminservice.applicationmonitoring.controller;

import com.prahlad.aijobportal.adminservice.feign.ApplicationServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Service's Application Monitoring feature
 * (DAY09_ADMIN_SERVICE.md): read-only Platform Application Statistics,
 * fetched live from Application Service via
 * {@code ApplicationServiceClient}. No service layer needed here beyond
 * the Feign client itself — there is no mutation, audit entry, or event
 * to add on top of a single passthrough read.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/applications")
@RequiredArgsConstructor
@Tag(name = "Admin - Application Monitoring")
public class ApplicationMonitoringController {

    private final ApplicationServiceClient applicationServiceClient;

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide application statistics")
    @CircuitBreaker(name = "applicationService")
    @Retry(name = "applicationService")
    public ResponseEntity<ApiResponse<ApplicationStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(applicationServiceClient.getStatistics().getData()));
    }
}

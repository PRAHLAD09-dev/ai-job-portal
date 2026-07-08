package com.prahlad.aijobportal.adminservice.notificationmonitoring.controller;

import com.prahlad.aijobportal.adminservice.feign.NotificationServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.NotificationStatisticsResponse;
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
 * Admin Service's Notification Monitoring feature
 * (DAY09_ADMIN_SERVICE.md): read-only Notification Statistics, fetched
 * live from Notification Service via {@code NotificationServiceClient}.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin - Notification Monitoring")
public class NotificationMonitoringController {

    private final NotificationServiceClient notificationServiceClient;

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide notification statistics")
    @CircuitBreaker(name = "notificationService")
    @Retry(name = "notificationService")
    public ResponseEntity<ApiResponse<NotificationStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(notificationServiceClient.getStatistics().getData()));
    }
}

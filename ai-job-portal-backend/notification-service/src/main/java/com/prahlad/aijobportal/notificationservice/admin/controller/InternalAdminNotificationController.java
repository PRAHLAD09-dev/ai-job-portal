package com.prahlad.aijobportal.notificationservice.admin.controller;

import com.prahlad.aijobportal.notificationservice.admin.dto.response.NotificationPlatformStatisticsResponse;
import com.prahlad.aijobportal.notificationservice.admin.service.AdminNotificationService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal-only, service-to-service endpoint backing Admin Service's
 * Notification Monitoring feature (DAY09_ADMIN_SERVICE.md). Never
 * routed through the API Gateway and never callable with a normal user
 * bearer token — authenticated exclusively by
 * {@code InternalServiceAuthFilter} via the shared
 * {@code X-Internal-Service-Token} header. Read-only; existing
 * user-facing notification endpoints are untouched.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/notifications/internal/admin")
@RequiredArgsConstructor
@Tag(name = "Internal - Admin", description = "Service-to-service endpoints for Admin Service, not exposed through the API Gateway")
public class InternalAdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide notification statistics (internal callers only)")
    public ResponseEntity<ApiResponse<NotificationPlatformStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminNotificationService.getPlatformStatistics()));
    }
}

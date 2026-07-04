package com.prahlad.aijobportal.notificationservice.notification.controller;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.notificationservice.notification.dto.request.NotificationPreferenceRequest;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationPreferenceResponse;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationPreferenceService;
import com.prahlad.aijobportal.notificationservice.security.principal.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/notifications/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Preferences", description = "Per-user notification channel preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    @Operation(summary = "Get the authenticated user notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> getMyPreferences(
            @AuthenticationPrincipal AuthenticatedUser user) {
        NotificationPreferenceResponse response = preferenceService.getMyPreferences(user.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update the authenticated user notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> updateMyPreferences(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody NotificationPreferenceRequest request) {
        NotificationPreferenceResponse response = preferenceService.updateMyPreferences(user.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", response));
    }
}

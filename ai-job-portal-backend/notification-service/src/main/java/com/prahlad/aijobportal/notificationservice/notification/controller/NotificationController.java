package com.prahlad.aijobportal.notificationservice.notification.controller;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationResponse;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.UnreadCountResponse;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationService;
import com.prahlad.aijobportal.notificationservice.security.principal.AuthenticatedUser;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification retrieval and read-state management")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get the authenticated user notifications, paginated")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<NotificationResponse> response = notificationService.getMyNotifications(user.userId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the authenticated user most recent notifications (Redis-backed)")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getLatestNotifications(
            @AuthenticationPrincipal AuthenticatedUser user) {
        List<NotificationResponse> response = notificationService.getLatestNotifications(user.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get the authenticated user unread notification count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal AuthenticatedUser user) {
        long count = notificationService.getUnreadCount(user.userId());
        return ResponseEntity.ok(ApiResponse.success(new UnreadCountResponse(count)));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID notificationId) {
        notificationService.markAsRead(user.userId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark every notification for the authenticated user as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal AuthenticatedUser user) {
        notificationService.markAllAsRead(user.userId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID notificationId) {
        notificationService.delete(user.userId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
    }
}

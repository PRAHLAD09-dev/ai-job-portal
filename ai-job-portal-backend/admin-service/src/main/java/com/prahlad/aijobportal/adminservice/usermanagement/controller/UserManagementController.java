package com.prahlad.aijobportal.adminservice.usermanagement.controller;

import com.prahlad.aijobportal.adminservice.feign.dto.AuthUserResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.adminservice.usermanagement.service.UserManagementService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin Service's User Management feature (DAY09_ADMIN_SERVICE.md).
 * Every operation here is a thin wrapper over Auth Service's internal
 * admin endpoints via {@code UserManagementService} — this controller
 * never touches a User entity/table directly. Permanently deleting a
 * user is restricted to {@code SUPER_ADMIN} since it is irreversible;
 * every other action is available to {@code ADMIN} and
 * {@code SUPER_ADMIN} alike (enforced by {@code SecurityConfig} at the
 * {@code /admin/**} path level).
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @Operation(summary = "List/search/filter platform users")
    public ResponseEntity<ApiResponse<PageResponse<AuthUserResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(userManagementService.searchUsers(keyword, role, status, page, size)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "View a single user")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(userManagementService.getUser(userId)));
    }

    @PatchMapping("/{userId}/enable")
    @Operation(summary = "Enable a user account")
    public ResponseEntity<ApiResponse<AuthUserResponse>> enableUser(@PathVariable UUID userId,
                                                                     @AuthenticationPrincipal AuthenticatedUser admin,
                                                                     HttpServletRequest request) {
        AuthUserResponse user = userManagementService.enableUser(userId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully", user));
    }

    @PatchMapping("/{userId}/disable")
    @Operation(summary = "Disable a user account")
    public ResponseEntity<ApiResponse<AuthUserResponse>> disableUser(@PathVariable UUID userId,
                                                                      @AuthenticationPrincipal AuthenticatedUser admin,
                                                                      HttpServletRequest request) {
        AuthUserResponse user = userManagementService.disableUser(userId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully", user));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Permanently delete a user account (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId,
                                                         @AuthenticationPrincipal AuthenticatedUser admin,
                                                         HttpServletRequest request) {
        userManagementService.deleteUser(userId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}

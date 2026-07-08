package com.prahlad.aijobportal.authservice.admin.controller;

import com.prahlad.aijobportal.authservice.admin.dto.response.AdminUserResponse;
import com.prahlad.aijobportal.authservice.admin.dto.response.UserPlatformStatisticsResponse;
import com.prahlad.aijobportal.authservice.admin.service.AdminUserService;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal-only, service-to-service endpoints backing Admin Service's User
 * Management and Dashboard features (DAY09_ADMIN_SERVICE.md). Never routed
 * through the API Gateway and never callable with a normal user bearer
 * token — authenticated exclusively by {@code InternalServiceAuthFilter}
 * via the shared {@code X-Internal-Service-Token} header, exactly like the
 * existing {@code InternalUserController}. Reuses {@code AdminUserService}
 * (backed by the same {@code User} entity/table) rather than duplicating
 * any Auth Service business logic.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/auth/internal/admin/users")
@RequiredArgsConstructor
@Tag(name = "Internal - Admin", description = "Service-to-service endpoints for Admin Service, not exposed through the API Gateway")
public class InternalAdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List/search/filter users (internal callers only)")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RoleName role,
            @RequestParam(required = false) AccountStatus status,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AdminUserResponse> page = adminUserService.searchUsers(keyword, role, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide user statistics (internal callers only)")
    public ResponseEntity<ApiResponse<UserPlatformStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getPlatformStatistics()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "View a single user's admin profile (internal callers only)")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUser(userId)));
    }

    @PatchMapping("/{userId}/enable")
    @Operation(summary = "Enable a user account (internal callers only)")
    public ResponseEntity<ApiResponse<AdminUserResponse>> enableUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully", adminUserService.enableUser(userId)));
    }

    @PatchMapping("/{userId}/disable")
    @Operation(summary = "Disable a user account (internal callers only)")
    public ResponseEntity<ApiResponse<AdminUserResponse>> disableUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully", adminUserService.disableUser(userId)));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Permanently delete a user account (internal callers only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}

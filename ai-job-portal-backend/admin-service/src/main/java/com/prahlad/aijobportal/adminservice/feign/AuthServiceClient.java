package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.AuthUserResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.UserGrowthPointResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.UserStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Synchronous Feign client to Auth Service's internal-only admin
 * endpoints (never routed through the API Gateway). Authenticates via
 * {@link FeignClientConfig}'s internal-service-token interceptor —
 * never the caller's own bearer token, since a platform administrator's
 * token is not an Auth Service credential. Used exclusively to back
 * Admin Service's User Management and Dashboard features
 * (DAY09_ADMIN_SERVICE.md); never duplicates Auth Service's own
 * registration/login business logic.
 */
@FeignClient(name = "AUTH-SERVICE", url = "${services.auth-service.url:}", path = CommonConstants.API_BASE_PATH + "/auth/internal/admin/users", configuration = FeignClientConfig.class)
public interface AuthServiceClient {

    @GetMapping
    ApiResponse<PageResponse<AuthUserResponse>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam int page,
            @RequestParam int size);

    @GetMapping("/statistics")
    ApiResponse<UserStatisticsResponse> getStatistics();

    /** DAY12 "Admin Dashboard: User Growth" — daily signup counts for the last {@code days} days. */
    @GetMapping("/growth")
    ApiResponse<List<UserGrowthPointResponse>> getUserGrowth(@RequestParam int days);

    @GetMapping("/{userId}")
    ApiResponse<AuthUserResponse> getUser(@PathVariable("userId") UUID userId);

    @PatchMapping("/{userId}/enable")
    ApiResponse<AuthUserResponse> enableUser(@PathVariable("userId") UUID userId);

    @PatchMapping("/{userId}/disable")
    ApiResponse<AuthUserResponse> disableUser(@PathVariable("userId") UUID userId);

    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable("userId") UUID userId);
}

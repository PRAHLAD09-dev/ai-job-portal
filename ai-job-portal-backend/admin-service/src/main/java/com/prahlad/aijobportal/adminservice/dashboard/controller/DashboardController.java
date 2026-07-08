package com.prahlad.aijobportal.adminservice.dashboard.controller;

import com.prahlad.aijobportal.adminservice.dashboard.dto.response.DashboardResponse;
import com.prahlad.aijobportal.adminservice.dashboard.service.DashboardService;
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
 * Admin Service's Dashboard feature (DAY09_ADMIN_SERVICE.md): platform
 * statistics (Total Users, Recruiters, Candidates, Companies, Jobs,
 * Applications) + Recent Activity, all fetched live.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin - Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get aggregated platform dashboard statistics and recent activity")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard()));
    }
}

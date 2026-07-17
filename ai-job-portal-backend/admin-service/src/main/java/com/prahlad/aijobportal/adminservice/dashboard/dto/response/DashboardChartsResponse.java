package com.prahlad.aijobportal.adminservice.dashboard.dto.response;

import com.prahlad.aijobportal.adminservice.feign.dto.UserGrowthPointResponse;

import java.util.List;

/**
 * DAY12 "Admin Dashboard: Add Charts" — chart-ready data for
 * {@code GET /admin/dashboard/charts}. Every series here is either real
 * time-series data (userGrowth, backed by Auth Service's actual signup
 * timestamps) or a real current-state breakdown already returned by an
 * owning service's existing statistics endpoint (companyVerification,
 * jobsByStatus, applicationsByStatus, aiUsageByFeature) — nothing is
 * estimated or fabricated. True historical trend lines for companies,
 * jobs, applications, and AI usage would need each owning service
 * (Recruiter/Job/Application/AI Service) to track daily snapshots, which
 * is out of DAY12's approved-modification scope (auth-service,
 * notification-service, admin-service, frontend only); this is the
 * complete, honest chart set obtainable within that scope today.
 */
public record DashboardChartsResponse(
        List<UserGrowthPointResponse> userGrowth,
        List<ChartDataPoint> companyVerification,
        List<ChartDataPoint> jobsByStatus,
        List<ChartDataPoint> applicationsByStatus,
        List<ChartDataPoint> aiUsageByFeature
) {
}

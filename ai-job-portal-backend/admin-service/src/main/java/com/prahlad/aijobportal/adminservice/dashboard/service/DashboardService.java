package com.prahlad.aijobportal.adminservice.dashboard.service;

import com.prahlad.aijobportal.adminservice.dashboard.dto.response.DashboardChartsResponse;
import com.prahlad.aijobportal.adminservice.dashboard.dto.response.DashboardResponse;

/**
 * Backs Admin Service's Dashboard feature (DAY09_ADMIN_SERVICE.md).
 * Concurrently fetches every platform statistic from its owning service
 * and combines it with this service's own recent-activity audit trail.
 */
public interface DashboardService {

    DashboardResponse getDashboard();

    /** DAY12 "Admin Dashboard: Add Charts". */
    DashboardChartsResponse getDashboardCharts(int userGrowthDays);
}

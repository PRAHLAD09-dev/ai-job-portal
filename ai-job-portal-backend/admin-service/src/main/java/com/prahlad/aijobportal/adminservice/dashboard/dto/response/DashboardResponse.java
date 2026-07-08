package com.prahlad.aijobportal.adminservice.dashboard.dto.response;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.AiStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.JobStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.NotificationStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.UserStatisticsResponse;

import java.util.List;

/**
 * Aggregates every platform statistic (Total Users, Total Recruiters,
 * Total Candidates, Total Companies, Total Jobs, Total Applications) +
 * Recent Activity into a single payload for Admin Service's Dashboard
 * feature (DAY09_ADMIN_SERVICE.md). Every field is populated from a live
 * call to the owning service (or, for recent activity, this service's
 * own audit log) — nothing here is cached/duplicated state.
 */
public record DashboardResponse(
        UserStatisticsResponse userStatistics,
        CompanyStatisticsResponse companyStatistics,
        JobStatisticsResponse jobStatistics,
        ApplicationStatisticsResponse applicationStatistics,
        AiStatisticsResponse aiStatistics,
        NotificationStatisticsResponse notificationStatistics,
        List<AuditLogResponse> recentActivity
) {
}

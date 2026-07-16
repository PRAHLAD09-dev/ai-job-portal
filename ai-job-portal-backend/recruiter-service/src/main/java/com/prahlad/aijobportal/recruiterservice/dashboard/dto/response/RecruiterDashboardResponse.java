package com.prahlad.aijobportal.recruiterservice.dashboard.dto.response;

import com.prahlad.aijobportal.recruiterservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.JobSavedCountResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.JobStatisticsResponse;

import java.util.List;

/**
 * DAY11 "Recruiter Dashboard Improvements": aggregates AI Match, Viewed
 * Status, and Saved Job Statistics — sourced live from AI Service, Job
 * Service, and Application Service via Feign, never persisted or
 * duplicated in Recruiter Service's own database.
 */
public record RecruiterDashboardResponse(
        JobStatisticsResponse jobStatistics,
        ApplicationStatisticsResponse applicationStatistics,
        List<JobSavedCountResponse> savedJobStatistics,
        List<RecentApplicationInsightResponse> recentApplications
) {
}

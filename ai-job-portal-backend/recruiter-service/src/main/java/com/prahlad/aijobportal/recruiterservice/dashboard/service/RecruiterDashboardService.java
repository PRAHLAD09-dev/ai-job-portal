package com.prahlad.aijobportal.recruiterservice.dashboard.service;

import com.prahlad.aijobportal.recruiterservice.dashboard.dto.response.RecruiterDashboardResponse;

/**
 * DAY11 "Recruiter Dashboard Improvements". Extends recruiter-service
 * only (no new microservice) and aggregates read-only data already
 * owned by Job Service, Application Service, and AI Service via Feign
 * — it never duplicates or persists their data locally.
 */
public interface RecruiterDashboardService {

    RecruiterDashboardResponse getDashboard(String bearerToken);
}

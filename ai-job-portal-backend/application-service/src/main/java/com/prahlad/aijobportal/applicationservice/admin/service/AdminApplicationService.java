package com.prahlad.aijobportal.applicationservice.admin.service;

import com.prahlad.aijobportal.applicationservice.admin.dto.response.ApplicationPlatformStatisticsResponse;

/**
 * Backs Application Service's internal admin statistics endpoint, called
 * exclusively by Admin Service via Feign + the shared internal-service
 * token. Read-only: reuses the existing
 * {@code JobApplicationRepository} count queries rather than duplicating
 * any Application Service business logic.
 */
public interface AdminApplicationService {

    ApplicationPlatformStatisticsResponse getPlatformStatistics();
}

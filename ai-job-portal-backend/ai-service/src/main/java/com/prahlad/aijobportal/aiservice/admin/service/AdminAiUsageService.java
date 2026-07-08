package com.prahlad.aijobportal.aiservice.admin.service;

import com.prahlad.aijobportal.aiservice.admin.dto.response.AiUsageStatisticsResponse;

/**
 * Backs AI Service's internal admin statistics endpoint, called
 * exclusively by Admin Service via Feign + the shared internal-service
 * token. Read-only: reuses the existing repositories' inherited
 * {@code count()} rather than duplicating any AI Service business logic.
 */
public interface AdminAiUsageService {

    AiUsageStatisticsResponse getUsageStatistics();
}

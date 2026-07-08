package com.prahlad.aijobportal.aiservice.admin.dto.response;

/**
 * Platform-wide AI feature usage counts, consumed by Admin Service's AI
 * Monitoring feature (AI Usage Statistics), per DAY09_ADMIN_SERVICE.md.
 */
public record AiUsageStatisticsResponse(
        long totalResumeAnalyses,
        long totalJobRecommendations,
        long totalInterviewQuestionSets
) {
}

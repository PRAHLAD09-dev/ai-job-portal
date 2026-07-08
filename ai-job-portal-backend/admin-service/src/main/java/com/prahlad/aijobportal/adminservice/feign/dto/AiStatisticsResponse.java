package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of AI Service's {@code AiUsageStatisticsResponse}
 * DTO, as returned by {@code GET /api/v1/ai/internal/admin/statistics}.
 */
public record AiStatisticsResponse(
        long totalResumeAnalyses,
        long totalJobRecommendations,
        long totalInterviewQuestionSets
) {
}

package com.prahlad.aijobportal.aiservice.recommendation.dto.response;

import java.util.UUID;

public record JobRecommendationResponse(
        UUID jobId,
        String jobTitle,
        String companyName,
        int matchScore,
        String reasoning
) {
}

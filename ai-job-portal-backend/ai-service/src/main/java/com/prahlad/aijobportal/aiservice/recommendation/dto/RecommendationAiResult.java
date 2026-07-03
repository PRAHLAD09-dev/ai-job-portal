package com.prahlad.aijobportal.aiservice.recommendation.dto;

import java.util.List;

/**
 * Deserialization target for the structured JSON Gemini is prompted to
 * return for both job recommendations (candidate side) and candidate
 * recommendations (recruiter side) — the shape is identical (rank a
 * given pool of {@code id}s against a profile), only the prompt and
 * the id's meaning differ.
 */
public record RecommendationAiResult(
        List<Item> recommendations
) {
    public record Item(String id, int matchScore, String reasoning) {
    }
}

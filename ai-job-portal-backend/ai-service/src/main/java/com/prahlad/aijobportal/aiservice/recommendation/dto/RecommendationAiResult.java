package com.prahlad.aijobportal.aiservice.recommendation.dto;

import java.util.List;

/**
 * Deserialization target for the structured JSON Gemini is prompted to
 * return for both job recommendations (candidate side) and candidate
 * recommendations (recruiter side) — the shape is identical (rank a
 * given pool of {@code id}s against a profile), only the prompt and
 * the id's meaning differ.
 *
 * <p>DAY10 (AI Enhancement & ATS Intelligence): {@code matchScore} is
 * now an AI-weighted overall percentage backed by six explicit
 * dimension scores (skill/experience/education/project/salary/location),
 * and {@code reasoning} is a list of short explainable bullet points
 * (e.g. "Strong Java experience", "Missing Kafka experience") instead
 * of a single freeform sentence. {@code strengths}, {@code weaknesses},
 * {@code missingSkills}, and {@code hiringRecommendation} are only
 * requested (and only populated) for the recruiter-facing candidate
 * ranking - the job-recommendation prompt never asks for them, so they
 * are simply left {@code null} on that path and ignored by
 * {@code JobRecommendationResponse}, which doesn't carry them.
 */
public record RecommendationAiResult(
        List<Item> recommendations
) {
    public record Item(
            String id,
            int matchScore,
            int skillMatch,
            int experienceMatch,
            int educationMatch,
            int projectMatch,
            int salaryMatch,
            int locationMatch,
            List<String> reasoning,
            List<String> strengths,
            List<String> weaknesses,
            List<String> missingSkills,
            String hiringRecommendation) {
    }
}

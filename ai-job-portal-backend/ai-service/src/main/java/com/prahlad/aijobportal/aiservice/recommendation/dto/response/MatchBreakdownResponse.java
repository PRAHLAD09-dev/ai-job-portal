package com.prahlad.aijobportal.aiservice.recommendation.dto.response;

/**
 * Explainable, per-dimension breakdown (0-100 each) behind an overall
 * AI match score, per DAY10_AI_Enhancement_ATS_Intelligence.md's
 * "AI Job Match %" and "Explainable AI" sections. Shared shape for
 * both the candidate-facing job match and the recruiter-facing
 * candidate match, since the six dimensions apply symmetrically.
 */
public record MatchBreakdownResponse(
        int skillMatch,
        int experienceMatch,
        int educationMatch,
        int projectMatch,
        int salaryMatch,
        int locationMatch
) {
}

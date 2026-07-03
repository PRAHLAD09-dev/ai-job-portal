package com.prahlad.aijobportal.aiservice.feign.dto;

import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Candidate Service's {@code CandidateProfileResponse}
 * (the caller's OWN profile — Candidate Service exposes no
 * cross-candidate lookup), used as AI prompt context for resume
 * analysis, job recommendations, and skill gap analysis.
 */
public record CandidateProfileSummaryResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        String headline,
        String summary,
        String city,
        String state,
        String country,
        List<SkillSummaryResponse> skills,
        List<ExperienceSummaryResponse> experiences,
        List<ResumeSummaryResponse> resumes
) {

    public record SkillSummaryResponse(String name, String proficiency, Integer yearsOfExperience) {
    }

    public record ExperienceSummaryResponse(String jobTitle, String companyName, String description) {
    }

    public record ResumeSummaryResponse(UUID id, String fileName, String fileUrl) {
    }
}

package com.prahlad.aijobportal.aiservice.feign.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Candidate Service's {@code CandidateProfileResponse}
 * (the caller's OWN profile — Candidate Service exposes no
 * cross-candidate lookup), used as AI prompt context for resume
 * analysis, job recommendations, and skill gap analysis.
 *
 * <p>Extended for DAY10 (AI Enhancement & ATS Intelligence) to carry
 * education history and dated experience entries so the AI job-match
 * prompt can reason about Experience Match and Education Match
 * dimensions, not just skills. These fields already exist on Candidate
 * Service's real response — Candidate Service itself is untouched
 * (DAY10's "Approved Modifications" excludes it); Spring's default
 * Jackson config ignores any producer fields still not mapped here.
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
        List<EducationSummaryResponse> educations,
        List<ResumeSummaryResponse> resumes
) {

    public record SkillSummaryResponse(String name, String proficiency, Integer yearsOfExperience) {
    }

    public record ExperienceSummaryResponse(
            String jobTitle,
            String companyName,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            boolean currentlyWorking) {
    }

    public record EducationSummaryResponse(
            String institutionName,
            String degreeType,
            String fieldOfStudy,
            LocalDate endDate,
            boolean currentlyStudying) {
    }

    public record ResumeSummaryResponse(UUID id, String fileName, String fileUrl) {
    }
}

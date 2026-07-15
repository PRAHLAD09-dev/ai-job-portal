package com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * {@code professionalSummary}, {@code projects}, {@code certifications},
 * {@code languages}, and {@code achievements} are extracted by Gemini
 * directly from the uploaded resume text on every fresh analysis, per
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements" section. They are AI output only - the
 * {@link com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis}
 * entity is not extended to store them (no schema change), so they are
 * populated only when this response is built straight from a new
 * Gemini call. A response served from a persisted lookup (a duplicate
 * resume-text hit in {@code analyze}, or {@code getLatestForCandidate})
 * carries these fields as empty, since they were never persisted.
 */
public record ResumeAnalysisResponse(
        UUID id,
        UUID candidateId,
        String resumeUrl,
        int atsScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingSkills,
        List<String> recommendations,
        Instant createdAt,
        String professionalSummary,
        List<String> projects,
        List<String> certifications,
        List<String> languages,
        List<String> achievements
) {
}

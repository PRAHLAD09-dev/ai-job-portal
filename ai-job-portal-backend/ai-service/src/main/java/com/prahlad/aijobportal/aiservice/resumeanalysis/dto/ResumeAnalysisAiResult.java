package com.prahlad.aijobportal.aiservice.resumeanalysis.dto;

import java.util.List;

/**
 * Deserialization target for the structured JSON Gemini is prompted to
 * return for a resume analysis. Internal to this feature — never
 * exposed on the API surface directly; {@code ResumeAnalysisMapper}
 * converts a persisted {@link com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis}
 * into the public {@code ResumeAnalysisResponse}.
 */
public record ResumeAnalysisAiResult(
        int atsScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingSkills,
        List<String> recommendations
) {
}

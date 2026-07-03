package com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response;

import java.util.List;

/**
 * Lightweight ATS compatibility check — {@code POST /api/v1/ai/resume/score}
 * — distinct from the full {@code ResumeAnalysisResponse}: a quick
 * score + formatting issues, without persistence, strengths/weaknesses,
 * or a Kafka event.
 */
public record ATSScoreResponse(
        int atsScore,
        List<String> formattingIssues,
        List<String> keywordGaps
) {
}

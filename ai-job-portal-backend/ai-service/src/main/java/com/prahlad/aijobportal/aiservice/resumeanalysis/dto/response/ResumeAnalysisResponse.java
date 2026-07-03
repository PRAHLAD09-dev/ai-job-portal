package com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResumeAnalysisResponse(
        UUID id,
        UUID candidateId,
        String resumeUrl,
        int atsScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingSkills,
        List<String> recommendations,
        Instant createdAt
) {
}

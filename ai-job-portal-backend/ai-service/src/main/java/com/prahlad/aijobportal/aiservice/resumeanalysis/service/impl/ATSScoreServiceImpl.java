package com.prahlad.aijobportal.aiservice.resumeanalysis.service.impl;

import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ATSScoreResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ATSScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Fast, unpersisted ATS compatibility check — {@code POST /api/v1/ai/resume/score}.
 * Deliberately does not persist or publish events; that is the fuller
 * {@code ResumeAnalysisService.analyze} flow's responsibility.
 */
@Service
@RequiredArgsConstructor
public class ATSScoreServiceImpl implements ATSScoreService {

    private static final String PROMPT_TEMPLATE = """
            You are an ATS (Applicant Tracking System) compatibility checker.
            Evaluate the following resume text purely for ATS parseability and
            keyword coverage (not overall quality). Return a JSON object with
            these exact fields:
            - atsScore: an integer from 0 to 100 representing ATS compatibility
            - formattingIssues: an array of short strings describing formatting problems that could confuse an ATS parser (e.g. tables, columns, unusual fonts, missing section headers)
            - keywordGaps: an array of short strings naming common industry keywords/skills likely missing from this resume

            Resume text:
            %s
            """;

    private final AiStructuredResponseService aiStructuredResponseService;

    @Override
    public ATSScoreResponse score(AnalyzeResumeRequest request) {
        ATSScoreResponse result = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(request.resumeText()), ATSScoreResponse.class);

        int clampedScore = Math.max(0, Math.min(100, result.atsScore()));
        return new ATSScoreResponse(clampedScore, result.formattingIssues(), result.keywordGaps());
    }
}

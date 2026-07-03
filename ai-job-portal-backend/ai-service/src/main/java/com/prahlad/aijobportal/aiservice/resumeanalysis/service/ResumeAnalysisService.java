package com.prahlad.aijobportal.aiservice.resumeanalysis.service;

import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;

import java.util.UUID;

public interface ResumeAnalysisService {

    /**
     * Analyzes a candidate's resume via Gemini. If an analysis already
     * exists for the exact same resume text (per DAY07_AI_SERVICE.md's
     * "No duplicate analysis" rule), the existing result is returned
     * instead of calling the AI provider again.
     */
    ResumeAnalysisResponse analyze(UUID candidateId, AnalyzeResumeRequest request);

    ResumeAnalysisResponse getLatestForCandidate(UUID candidateId);
}

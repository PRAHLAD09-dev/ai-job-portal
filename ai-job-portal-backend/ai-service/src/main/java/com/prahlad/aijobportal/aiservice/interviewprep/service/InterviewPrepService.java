package com.prahlad.aijobportal.aiservice.interviewprep.service;

import com.prahlad.aijobportal.aiservice.interviewprep.dto.request.GenerateInterviewPrepRequest;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.DetectedTopicsResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.InterviewPrepQuestionSetResponse;

import java.util.UUID;

public interface InterviewPrepService {

    /**
     * Detects practiceable topics (skills/technologies and project
     * names) from the authenticated candidate's latest resume, for the
     * frontend to render as selectable chips (PRD Step 2). Requires the
     * candidate to have already run resume analysis at least once (the
     * standard flow, since Interview Generator sits after it in the AI
     * module) - throws {@code ResourceNotFoundException} otherwise.
     */
    DetectedTopicsResponse detectTopics(UUID candidateId);

    /**
     * Generates and persists a fresh set of resume-based interview
     * practice questions. Also used for "Regenerate" (PRD Step 8) - the
     * frontend simply resubmits the same request; this always calls the
     * AI provider fresh rather than reusing a cached result, since
     * "different questions" is the explicit point of regenerating.
     */
    InterviewPrepQuestionSetResponse generate(UUID candidateId, GenerateInterviewPrepRequest request);

    /** The candidate's most recently generated question set, if any. */
    InterviewPrepQuestionSetResponse getLatest(UUID candidateId);

    /** Renders a previously generated question set (owned by this candidate) as a downloadable PDF. */
    byte[] generatePdf(UUID candidateId, UUID questionSetId);
}

package com.prahlad.aijobportal.aiservice.interview.service;

import com.prahlad.aijobportal.aiservice.interview.dto.request.GenerateInterviewQuestionsRequest;
import com.prahlad.aijobportal.aiservice.interview.dto.response.InterviewQuestionResponse;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionService {

    /** Generates and persists new interview questions for a job owned by the authenticated recruiter's company. */
    List<InterviewQuestionResponse> generate(UUID recruiterUserId, String bearerToken, GenerateInterviewQuestionsRequest request);

    List<InterviewQuestionResponse> getForJob(UUID jobId);
}

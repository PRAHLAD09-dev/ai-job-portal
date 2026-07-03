package com.prahlad.aijobportal.aiservice.interview.dto.response;

import java.util.UUID;

public record InterviewQuestionResponse(
        UUID id,
        UUID jobId,
        String question,
        String difficulty,
        String category
) {
}

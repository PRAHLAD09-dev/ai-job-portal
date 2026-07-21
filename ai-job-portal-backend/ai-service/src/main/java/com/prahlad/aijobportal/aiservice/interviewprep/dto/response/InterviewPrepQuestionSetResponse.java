package com.prahlad.aijobportal.aiservice.interviewprep.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A generated interview practice question set, grouped by topic
 * (e.g. "Java (5 Questions)", "AI Job Portal (8 Questions)") per the
 * PRD's grouping enhancement - each {@code TopicQuestionsResponse}'s
 * question count for the UI is simply {@code questions().size()}.
 */
public record InterviewPrepQuestionSetResponse(
        UUID id,
        List<String> selectedTopics,
        String difficulty,
        String questionType,
        int totalQuestions,
        List<TopicQuestionsResponse> sections,
        Instant generatedAt
) {
    public record TopicQuestionsResponse(String topic, List<String> questions) {
    }
}

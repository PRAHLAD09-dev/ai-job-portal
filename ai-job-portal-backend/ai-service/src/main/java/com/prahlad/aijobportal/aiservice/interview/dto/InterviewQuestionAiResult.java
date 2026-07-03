package com.prahlad.aijobportal.aiservice.interview.dto;

import java.util.List;

public record InterviewQuestionAiResult(
        List<Item> questions
) {
    public record Item(String question, String difficulty, String category) {
    }
}

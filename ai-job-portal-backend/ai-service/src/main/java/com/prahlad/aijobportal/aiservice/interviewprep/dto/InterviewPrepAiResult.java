package com.prahlad.aijobportal.aiservice.interviewprep.dto;

import java.util.List;

/**
 * Deserialization target for the structured JSON Gemini is prompted to
 * return for a resume-based interview practice question set. Each item
 * is tagged with the topic it was generated for (a selected skill,
 * technology, or project name) so the response/PDF can group questions
 * into sections, per the PRD's "grouping" enhancement. Internal to this
 * feature - never exposed on the API surface directly;
 * {@code InterviewPrepMapper} groups it into
 * {@code InterviewPrepQuestionSetResponse}.
 */
public record InterviewPrepAiResult(
        List<Item> questions
) {
    public record Item(String topic, String question) {
    }
}

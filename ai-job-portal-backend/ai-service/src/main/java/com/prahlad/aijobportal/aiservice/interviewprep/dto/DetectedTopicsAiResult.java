package com.prahlad.aijobportal.aiservice.interviewprep.dto;

import java.util.List;

/**
 * Deserialization target for the structured JSON Gemini is prompted to
 * return when extracting practiceable topics (skills/technologies and
 * project names) from a candidate's resume text, per the AI Interview
 * Generator PRD's "Detect categories from the resume" step. Internal to
 * this feature - never exposed on the API surface directly;
 * {@code InterviewPrepServiceImpl} maps it into
 * {@code DetectedTopicsResponse}.
 */
public record DetectedTopicsAiResult(
        List<String> skills,
        List<String> projects
) {
}

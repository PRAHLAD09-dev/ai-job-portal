package com.prahlad.aijobportal.aiservice.gemini.dto;

import java.util.List;

/**
 * Mirrors the response body of Gemini's
 * {@code models/{model}:generateContent} REST endpoint. Only the
 * fields this service actually reads are declared; anything else in
 * the real response is ignored by Jackson at deserialization time.
 */
public record GeminiGenerateContentResponse(
        List<Candidate> candidates
) {
    public record Candidate(Content content, String finishReason) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }
}

package com.prahlad.aijobportal.aiservice.gemini.dto;

import java.util.List;

/**
 * Mirrors the request body of Gemini's
 * {@code models/{model}:generateContent} REST endpoint. Kept minimal —
 * only the fields this service actually sets.
 */
public record GeminiGenerateContentRequest(
        List<Content> contents,
        GenerationConfig generationConfig
) {
    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record GenerationConfig(double temperature, int maxOutputTokens) {
    }
}

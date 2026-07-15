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

    /**
     * {@code thinkingConfig} disables Gemini 2.5's internal "thinking"
     * tokens for these calls. Thinking tokens are counted against
     * {@code maxOutputTokens} just like the visible answer, but aren't
     * returned as text - for a plain "return this JSON" prompt with no
     * multi-step reasoning need, letting the model think first can burn
     * most or all of the token budget before it writes a single
     * character of the actual JSON response, silently truncating the
     * output. See DAY10_AI_Enhancement_ATS_Intelligence.md's richer
     * response schemas (skill gap priority order, resume extraction
     * fields, etc.) for why this started showing up in practice: the
     * JSON got bigger, so there was less room for it to survive
     * thinking eating the budget first.
     */
    public record GenerationConfig(double temperature, int maxOutputTokens, ThinkingConfig thinkingConfig) {
    }

    public record ThinkingConfig(int thinkingBudget) {
    }
}

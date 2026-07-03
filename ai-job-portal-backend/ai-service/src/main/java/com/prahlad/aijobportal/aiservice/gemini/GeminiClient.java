package com.prahlad.aijobportal.aiservice.gemini;

import java.util.concurrent.CompletableFuture;

/**
 * Provider-independent text generation abstraction, per DECISIONS.md
 * ("Provider-Independent Design"). Every AI feature in this service
 * depends on this interface, not on Gemini directly — swapping LLM
 * providers means writing a new implementation of this one interface.
 */
public interface GeminiClient {

    /**
     * Sends {@code prompt} to the configured LLM and returns the raw
     * generated text. Guarded by circuit breaker / retry / bulkhead /
     * time limiter at the implementation level.
     */
    CompletableFuture<String> generateText(String prompt);
}

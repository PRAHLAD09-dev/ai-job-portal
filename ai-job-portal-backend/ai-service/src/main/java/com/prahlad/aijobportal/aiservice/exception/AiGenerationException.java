package com.prahlad.aijobportal.aiservice.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Raised when the Gemini API call ultimately fails after retries (or
 * the circuit breaker is open), per DAY07_AI_SERVICE.md's "Retry AI
 * failures. Circuit Breaker. Fallback Responses." validation rules.
 */
public class AiGenerationException extends BusinessException {

    public AiGenerationException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "AI_GENERATION_FAILED");
    }

    public AiGenerationException(String message, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "AI_GENERATION_FAILED");
        initCause(cause);
    }
}

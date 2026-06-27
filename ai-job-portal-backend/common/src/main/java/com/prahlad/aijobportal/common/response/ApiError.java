package com.prahlad.aijobportal.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a single error item inside an {@link ApiResponse}.
 * Used for validation errors (field-level) and general business errors.
 * Generic and feature-agnostic by design.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    /**
     * The field that caused the error, when applicable (e.g. validation errors).
     * Null for non-field-specific errors.
     */
    private String field;

    /**
     * Machine-readable error code (e.g. "RESOURCE_NOT_FOUND", "FIELD_INVALID").
     */
    private String code;

    /**
     * Human-readable error message.
     */
    private String message;
}

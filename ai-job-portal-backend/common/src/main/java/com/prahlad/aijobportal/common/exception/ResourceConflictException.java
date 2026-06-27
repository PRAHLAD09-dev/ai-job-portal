package com.prahlad.aijobportal.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic "conflict" exception (HTTP 409) — e.g. duplicate email on
 * registration, candidate applying twice to the same job.
 * Generic and reusable; feature-specific wording is supplied by the caller.
 */
public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "RESOURCE_CONFLICT");
    }
}

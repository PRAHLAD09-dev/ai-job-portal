package com.prahlad.aijobportal.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic "resource not found" exception (HTTP 404).
 * Each microservice may throw this directly for simple lookups
 * (e.g. "Job not found with id: 123") without needing to declare
 * a brand-new exception class for every entity.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}

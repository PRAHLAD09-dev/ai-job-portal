package com.prahlad.aijobportal.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic "access denied" exception (HTTP 403) — e.g. a recruiter trying
 * to manage a job that belongs to a different company.
 */
public class AccessDeniedBusinessException extends BusinessException {

    public AccessDeniedBusinessException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}

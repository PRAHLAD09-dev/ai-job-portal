package com.prahlad.aijobportal.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base unchecked exception for business-rule violations.
 * Feature-specific exceptions in each microservice should extend this class
 * (e.g. CandidateNotFoundException, DuplicateApplicationException), not this
 * common module, since per-feature exceptions belong to their own service.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorCode = "BUSINESS_RULE_VIOLATION";
    }

    public BusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

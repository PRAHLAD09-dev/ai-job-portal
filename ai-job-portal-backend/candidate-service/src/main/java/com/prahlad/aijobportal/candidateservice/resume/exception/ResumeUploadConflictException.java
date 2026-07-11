package com.prahlad.aijobportal.candidateservice.resume.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when two near-simultaneous resume uploads for the same
 * candidate race each other and the database's concurrency constraints
 * (see V2__resume_concurrency_constraints.sql: at most one ACTIVE resume
 * per candidate, unique version numbers per candidate) reject the loser.
 * A clean, actionable 409 instead of a raw constraint-violation 500.
 */
public class ResumeUploadConflictException extends BusinessException {

    public ResumeUploadConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "RESUME_UPLOAD_CONFLICT");
    }
}

package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.AccessDeniedBusinessException;

/**
 * Thrown when a recruiter attempts to manage a job that belongs to a
 * different company, per PROJECT_SPECIFICATION.md Section 16 (Business
 * Rules): "Recruiter can manage only their own company jobs."
 */
public class JobAccessDeniedException extends AccessDeniedBusinessException {

    public JobAccessDeniedException(String message) {
        super(message);
    }
}

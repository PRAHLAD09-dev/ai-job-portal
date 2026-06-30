package com.prahlad.aijobportal.recruiterservice.company.exception;

import com.prahlad.aijobportal.common.exception.AccessDeniedBusinessException;

/**
 * Thrown when a recruiter attempts to manage a company they do not
 * belong to, per PROJECT_SPECIFICATION.md Section 16 (Business Rules):
 * "Recruiter can manage only their own company jobs" — the same
 * ownership boundary applies to managing the company profile itself.
 */
public class RecruiterAccessDeniedException extends AccessDeniedBusinessException {

    public RecruiterAccessDeniedException(String message) {
        super(message);
    }
}

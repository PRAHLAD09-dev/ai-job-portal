package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a job status transition is not allowed from its current
 * state (e.g. publishing an already-closed job, closing a draft).
 */
public class InvalidJobStateException extends BusinessException {

    public InvalidJobStateException(String message) {
        super(message, HttpStatus.CONFLICT, "INVALID_JOB_STATE");
    }
}

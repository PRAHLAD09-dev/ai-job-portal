package com.prahlad.aijobportal.candidateservice.resume.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when an uploaded resume file fails validation — unsupported
 * format, exceeds the maximum allowed size, or is empty.
 */
public class InvalidResumeFileException extends BusinessException {

    public InvalidResumeFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_RESUME_FILE");
    }
}

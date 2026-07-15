package com.prahlad.aijobportal.aiservice.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Raised whenever a candidate's resume PDF can't be turned into usable
 * text: the file couldn't be downloaded, isn't a valid/readable PDF,
 * is password-protected, exceeds the configured size limit, or has no
 * extractable text layer (e.g. a scanned image with no OCR). HTTP 422
 * (Unprocessable Entity) — the request itself was well-formed, but the
 * referenced file can't be processed.
 */
public class ResumeExtractionException extends BusinessException {

    public ResumeExtractionException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "RESUME_EXTRACTION_FAILED");
    }

    public ResumeExtractionException(String message, Throwable cause) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "RESUME_EXTRACTION_FAILED");
        initCause(cause);
    }
}

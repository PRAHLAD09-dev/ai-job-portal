package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * DAY11 "Apply Methods": thrown when a candidate calls
 * {@code POST /applications} for a job whose {@code applyMethod} is
 * {@code EXTERNAL_APPLY}. Such jobs never get an in-app application
 * record — the candidate should instead be redirected to the job's
 * {@code externalApplyUrl}, obtained via
 * {@code GET /applications/apply-info/{jobId}}.
 */
public class ExternalApplyNotAllowedException extends BusinessException {

    public ExternalApplyNotAllowedException() {
        super("This job only accepts external applications. Use the external apply URL instead of submitting an in-app application.",
                HttpStatus.CONFLICT, "EXTERNAL_APPLY_NOT_ALLOWED");
    }
}

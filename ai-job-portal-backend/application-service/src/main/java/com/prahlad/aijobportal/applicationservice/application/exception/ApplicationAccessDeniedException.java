package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.AccessDeniedBusinessException;

public class ApplicationAccessDeniedException extends AccessDeniedBusinessException {

    public ApplicationAccessDeniedException(String message) {
        super(message);
    }
}

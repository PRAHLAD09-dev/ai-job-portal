package com.prahlad.aijobportal.aiservice.exception;

import com.prahlad.aijobportal.common.exception.AccessDeniedBusinessException;

public class AiAccessDeniedException extends AccessDeniedBusinessException {

    public AiAccessDeniedException(String message) {
        super(message);
    }
}

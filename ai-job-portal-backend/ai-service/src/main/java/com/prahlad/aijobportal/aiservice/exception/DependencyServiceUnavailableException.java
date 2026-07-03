package com.prahlad.aijobportal.aiservice.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DependencyServiceUnavailableException extends BusinessException {

    public DependencyServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "DEPENDENCY_SERVICE_UNAVAILABLE");
    }
}

package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidApplicationStateException extends BusinessException {

    public InvalidApplicationStateException(String message) {
        super(message, HttpStatus.CONFLICT, "INVALID_APPLICATION_STATE");
    }
}

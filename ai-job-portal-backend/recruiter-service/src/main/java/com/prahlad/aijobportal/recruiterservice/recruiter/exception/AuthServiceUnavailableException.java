package com.prahlad.aijobportal.recruiterservice.recruiter.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a synchronous call to Auth Service (via OpenFeign) fails
 * due to the service being unreachable or returning a server error.
 */
public class AuthServiceUnavailableException extends BusinessException {

    public AuthServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "AUTH_SERVICE_UNAVAILABLE");
    }
}

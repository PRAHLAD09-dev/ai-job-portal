package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a synchronous call to Auth Service or Recruiter Service
 * (via OpenFeign) fails due to the service being unreachable or
 * returning a server error.
 */
public class AuthServiceUnavailableException extends BusinessException {

    public AuthServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "DEPENDENCY_SERVICE_UNAVAILABLE");
    }
}

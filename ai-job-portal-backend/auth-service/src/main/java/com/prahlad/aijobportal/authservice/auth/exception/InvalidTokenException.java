package com.prahlad.aijobportal.authservice.auth.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a refresh token, e-mail verification token, or password
 * reset token is missing, already used, expired, or otherwise invalid.
 */
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
    }
}

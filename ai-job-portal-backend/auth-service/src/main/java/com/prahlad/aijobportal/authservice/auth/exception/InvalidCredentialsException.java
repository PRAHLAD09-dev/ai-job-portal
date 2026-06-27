package com.prahlad.aijobportal.authservice.auth.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when login credentials (email/password) do not match, or the
 * account state otherwise disallows authentication. Deliberately generic
 * in wording to avoid revealing whether the e-mail itself exists.
 */
public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}

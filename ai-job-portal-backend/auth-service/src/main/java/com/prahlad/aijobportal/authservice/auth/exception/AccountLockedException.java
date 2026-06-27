package com.prahlad.aijobportal.authservice.auth.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a login attempt is made against an account that has been
 * locked, either by an administrator (DISABLED) or automatically after
 * too many consecutive failed login attempts.
 */
public class AccountLockedException extends BusinessException {

    public AccountLockedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED");
    }
}

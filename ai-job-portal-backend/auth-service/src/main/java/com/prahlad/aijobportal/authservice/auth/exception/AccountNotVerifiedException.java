package com.prahlad.aijobportal.authservice.auth.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a login attempt is made against an account whose e-mail
 * address has not yet been verified.
 */
public class AccountNotVerifiedException extends BusinessException {

    public AccountNotVerifiedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCOUNT_NOT_VERIFIED");
    }
}

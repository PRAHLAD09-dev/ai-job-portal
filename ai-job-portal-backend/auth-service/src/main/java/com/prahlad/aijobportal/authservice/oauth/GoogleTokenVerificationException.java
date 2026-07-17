package com.prahlad.aijobportal.authservice.oauth;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a Google Sign-In ID token fails verification: bad
 * signature, wrong audience/issuer, expired, or the token's email isn't
 * Google-verified. Mapped to 401, matching {@code InvalidCredentialsException}
 * / {@code InvalidTokenException} — from the caller's point of view this
 * is just "that login attempt didn't work."
 */
public class GoogleTokenVerificationException extends BusinessException {

    public GoogleTokenVerificationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "GOOGLE_TOKEN_INVALID");
    }
}

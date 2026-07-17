package com.prahlad.aijobportal.authservice.oauth.dto;

/**
 * The verified, trustworthy claims extracted from a Google Sign-In ID
 * token, after {@code GoogleIdTokenVerifier} has confirmed the token's
 * signature, audience, issuer, and expiry. Nothing downstream needs to
 * (or should) touch the raw token again.
 */
public record GoogleIdTokenPayload(
        String googleId,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName
) {
}

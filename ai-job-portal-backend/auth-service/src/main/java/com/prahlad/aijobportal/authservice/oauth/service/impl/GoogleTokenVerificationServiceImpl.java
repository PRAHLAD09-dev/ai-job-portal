package com.prahlad.aijobportal.authservice.oauth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.prahlad.aijobportal.authservice.oauth.GoogleTokenVerificationException;
import com.prahlad.aijobportal.authservice.oauth.dto.GoogleIdTokenPayload;
import com.prahlad.aijobportal.authservice.oauth.service.GoogleTokenVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleTokenVerificationServiceImpl implements GoogleTokenVerificationService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public GoogleIdTokenPayload verify(String idToken) {
        GoogleIdToken token;
        try {
            token = googleIdTokenVerifier.verify(idToken);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            log.warn("Google ID token verification failed: {}", ex.getMessage());
            throw new GoogleTokenVerificationException("Google sign-in failed: could not verify token");
        }

        if (token == null) {
            // verify() returns null (rather than throwing) for a token that is
            // well-formed but fails signature/audience/issuer/expiry checks.
            throw new GoogleTokenVerificationException("Google sign-in failed: token is invalid or expired");
        }

        GoogleIdToken.Payload payload = token.getPayload();

        Boolean emailVerified = payload.getEmailVerified();
        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            throw new GoogleTokenVerificationException("Google account has no associated email address");
        }

        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        if (firstName == null || firstName.isBlank()) {
            // Some Google accounts (e.g. a bare Workspace alias) omit given_name/family_name;
            // fall back to splitting the always-present "name" claim rather than failing sign-in.
            String fullName = (String) payload.get("name");
            String[] parts = fullName != null ? fullName.trim().split("\\s+", 2) : new String[0];
            firstName = parts.length > 0 && !parts[0].isBlank() ? parts[0] : "Google";
            lastName = parts.length > 1 ? parts[1] : "User";
        }
        if (lastName == null || lastName.isBlank()) {
            lastName = "User";
        }

        return new GoogleIdTokenPayload(
                token.getPayload().getSubject(),
                email.trim().toLowerCase(),
                Boolean.TRUE.equals(emailVerified),
                firstName.trim(),
                lastName.trim()
        );
    }
}

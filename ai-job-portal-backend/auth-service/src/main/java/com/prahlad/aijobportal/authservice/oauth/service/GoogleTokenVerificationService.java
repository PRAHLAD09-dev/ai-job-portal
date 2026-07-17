package com.prahlad.aijobportal.authservice.oauth.service;

import com.prahlad.aijobportal.authservice.oauth.dto.GoogleIdTokenPayload;

/** DAY12 "Google OAuth" — verifies a Google Sign-In ID token and extracts its trustworthy claims. */
public interface GoogleTokenVerificationService {

    /**
     * @throws com.prahlad.aijobportal.authservice.oauth.GoogleTokenVerificationException
     *         if the token's signature, audience, issuer, or expiry don't check out.
     */
    GoogleIdTokenPayload verify(String idToken);
}

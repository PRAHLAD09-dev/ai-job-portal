package com.prahlad.aijobportal.authservice.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code google.oauth.*} — DAY12 "Google OAuth". Only the OAuth
 * client ID is needed server-side: it's the expected "audience" of a
 * Google Sign-In ID token, checked by {@code GoogleIdTokenVerifier}.
 * There is no client secret here on purpose - the ID-token flow this
 * service verifies is the browser/SPA flow (Google Identity Services),
 * which never involves a client secret.
 */
@Configuration
@ConfigurationProperties(prefix = "google.oauth")
@Getter
@Setter
public class GoogleOAuthProperties {

    private String clientId;
}

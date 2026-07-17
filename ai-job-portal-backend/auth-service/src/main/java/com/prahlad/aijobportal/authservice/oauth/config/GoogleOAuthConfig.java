package com.prahlad.aijobportal.authservice.oauth.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.prahlad.aijobportal.authservice.security.config.GoogleOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * DAY12 "Google OAuth". {@code GoogleIdTokenVerifier} fetches and caches
 * Google's public signing certificates itself (refreshing them
 * automatically as they rotate), so a single shared bean is exactly what
 * Google's own docs recommend - not one built per request.
 */
@Configuration
@RequiredArgsConstructor
public class GoogleOAuthConfig {

    private final GoogleOAuthProperties googleOAuthProperties;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleOAuthProperties.getClientId()))
                .build();
    }
}

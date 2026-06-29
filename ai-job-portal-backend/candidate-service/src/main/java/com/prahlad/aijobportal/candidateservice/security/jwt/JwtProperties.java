package com.prahlad.aijobportal.candidateservice.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.jwt.*} configuration properties. The Candidate Service
 * only ever VALIDATES access tokens issued by the Auth Service — it never
 * issues tokens itself, so only the access-token secret/issuer are
 * needed here (no refresh-token secret, no token-expiration settings to
 * configure on this side).
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key used by the Auth Service to sign access tokens (HS256).
     * Must be the exact same value configured on the Auth Service, since
     * verification requires the identical symmetric key.
     */
    private String secret;

    private String issuer;
}

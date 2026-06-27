package com.prahlad.aijobportal.authservice.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.jwt.*} configuration properties (sourced from the
 * Config Server / environment variables, never hardcoded).
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key used to sign access tokens (HS256). Must be at least
     * 256 bits (32 bytes) once Base64-decoded.
     */
    private String secret;

    /**
     * Secret key used to sign refresh tokens. Kept separate from the
     * access token secret so that a compromise of one token type does
     * not automatically compromise the other.
     */
    private String refreshSecret;

    /**
     * Access token lifetime, in milliseconds.
     */
    private long accessTokenExpirationMs;

    /**
     * Refresh token lifetime, in milliseconds.
     */
    private long refreshTokenExpirationMs;

    private String issuer;
}

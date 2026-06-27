package com.prahlad.aijobportal.authservice.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.auth.*} configuration properties: token lifetimes for
 * e-mail verification / password reset flows, and account lockout policy.
 */
@Configuration
@ConfigurationProperties(prefix = "app.auth")
@Getter
@Setter
public class AuthProperties {

    private long emailVerificationTokenExpirationMs;

    private long passwordResetTokenExpirationMs;

    private int maxFailedLoginAttempts;

    private String frontendVerifyEmailUrl;

    private String frontendResetPasswordUrl;
}

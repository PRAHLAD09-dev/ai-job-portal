package com.prahlad.aijobportal.recruiterservice.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.jwt.*} configuration properties. The Recruiter
 * Service only ever VALIDATES access tokens issued by the Auth Service —
 * it never issues tokens itself.
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key used by the Auth Service to sign access tokens (HS256).
     * Must be the exact same value configured on the Auth Service.
     */
    private String secret;

    private String issuer;
}

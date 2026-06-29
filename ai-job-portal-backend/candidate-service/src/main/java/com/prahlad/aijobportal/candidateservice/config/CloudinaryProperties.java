package com.prahlad.aijobportal.candidateservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.cloudinary.*} configuration properties — credentials
 * sourced exclusively from environment variables, never hardcoded.
 */
@Configuration
@ConfigurationProperties(prefix = "app.cloudinary")
@Getter
@Setter
public class CloudinaryProperties {

    private String cloudName;
    private String apiKey;
    private String apiSecret;
}

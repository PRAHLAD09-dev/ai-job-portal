package com.prahlad.aijobportal.aiservice.gemini;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.gemini")
@Getter
@Setter
public class GeminiProperties {

    /** Never logged, never returned in any response. */
    private String apiKey;

    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

    private String model = "gemini-2.5-flash";

    private double temperature = 0.4;

    private int maxOutputTokens = 2048;
}

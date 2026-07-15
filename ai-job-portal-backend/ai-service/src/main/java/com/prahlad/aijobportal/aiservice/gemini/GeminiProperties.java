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

    /**
     * 2048 was enough for the original, smaller response schemas. It
     * is not enough headroom for DAY10_AI_Enhancement_ATS_Intelligence.md's
     * richer ones (skill gap priority order + learning suggestions,
     * resume analysis extraction fields) - responses were observed
     * getting cut off mid-JSON at that limit. 8192 leaves comfortable
     * margin for the largest current schema (resume analysis) plus
     * future growth.
     */
    private int maxOutputTokens = 8192;
}

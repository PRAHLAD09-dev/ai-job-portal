package com.prahlad.aijobportal.candidateservice.resume.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Binds {@code app.resume.*} configuration properties: upload constraints
 * for resume files.
 */
@Configuration
@ConfigurationProperties(prefix = "app.resume")
@Getter
@Setter
public class ResumeProperties {

    private long maxFileSizeBytes;

    private List<String> allowedFormats;

    private String cloudinaryFolder;
}

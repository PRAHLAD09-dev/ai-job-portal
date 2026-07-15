package com.prahlad.aijobportal.aiservice.resumeanalysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Tunables for {@code ResumeTextExtractionServiceImpl}, per
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements" section. All have safe defaults (unlike
 * {@code GeminiProperties}'s secrets) so the service still starts if
 * config-repo doesn't override them.
 */
@Configuration
@ConfigurationProperties(prefix = "app.resume-extraction")
@Getter
@Setter
public class ResumeExtractionProperties {

    /** Resume PDFs larger than this are rejected before download completes. */
    private long maxFileSizeBytes = 10 * 1024 * 1024;

    /**
     * Below this many normalized characters, the PDF is treated as
     * empty or scanned-image-only (no extractable text layer) and
     * rejected with a clear message instead of being sent to Gemini.
     */
    private int minExtractedTextLength = 50;

    /** Matches the resumeText size limit the request DTO enforced previously. */
    private int maxExtractedTextLength = 50000;
}

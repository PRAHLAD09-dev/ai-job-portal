package com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Per DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements" section, the candidate supplies only the Cloudinary
 * URL of the resume PDF they uploaded via Candidate Service -
 * {@code ResumeTextExtractionService} downloads that file and extracts
 * its text server-side. The frontend must never be required to supply
 * pre-extracted resume text.
 */
public record AnalyzeResumeRequest(

        @NotBlank(message = "Resume URL is required")
        @Size(max = 1000, message = "Resume URL must not exceed 1000 characters")
        String resumeUrl
) {
}

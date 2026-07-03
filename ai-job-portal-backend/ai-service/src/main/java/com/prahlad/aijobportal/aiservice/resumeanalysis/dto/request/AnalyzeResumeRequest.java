package com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Per DECISIONS.md ("Resume Storage via Cloudinary URL"), the resume
 * file itself is never uploaded to this service — only its Cloudinary
 * URL plus the already-extracted plain text is sent for analysis.
 * Text extraction from the stored file is Candidate Service's
 * responsibility, not this service's.
 */
public record AnalyzeResumeRequest(

        @NotBlank(message = "Resume URL is required")
        @Size(max = 1000, message = "Resume URL must not exceed 1000 characters")
        String resumeUrl,

        @NotBlank(message = "Resume text is required")
        @Size(max = 50000, message = "Resume text must not exceed 50000 characters")
        String resumeText
) {
}

package com.prahlad.aijobportal.aiservice.coverletter.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoverLetterRequest(

        @NotNull(message = "Job ID is required")
        java.util.UUID jobId,

        @Size(max = 2000, message = "Additional notes must not exceed 2000 characters")
        String additionalNotes
) {
}

package com.prahlad.aijobportal.applicationservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Submitted by a candidate to apply for a job. {@code resumeId} is
 * optional — when omitted, the candidate's most recently uploaded
 * resume (from Candidate Service) is used, per DAY06's "Resume
 * Selection" feature.
 */
public record CreateApplicationRequest(

        @NotNull(message = "Job id is required")
        UUID jobId,

        UUID resumeId,

        @Size(max = 5000, message = "Cover letter must not exceed 5000 characters")
        String coverLetter
) {
}

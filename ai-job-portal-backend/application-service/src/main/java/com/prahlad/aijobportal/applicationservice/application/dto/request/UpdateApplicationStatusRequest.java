package com.prahlad.aijobportal.applicationservice.application.dto.request;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Generic recruiter-driven status transition request, used by the
 * shortlist / interview / offer / hire / reject convenience endpoints
 * as well as a direct status update. {@code interviewDate} is required
 * only when {@code status == INTERVIEW}; validated in the service
 * layer since it is conditional on another field.
 */
public record UpdateApplicationStatusRequest(

        @NotNull(message = "Status is required")
        ApplicationStatus status,

        Instant interviewDate,

        @Size(max = 2000, message = "Remarks must not exceed 2000 characters")
        String remarks
) {
}

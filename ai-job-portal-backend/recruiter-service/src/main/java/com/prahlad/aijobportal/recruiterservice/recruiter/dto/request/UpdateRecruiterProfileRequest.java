package com.prahlad.aijobportal.recruiterservice.recruiter.dto.request;

import com.prahlad.aijobportal.recruiterservice.recruiter.enums.RecruiterTitle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code PUT /recruiter/profile}. Identity fields
 * (email, name) remain owned exclusively by Auth Service and cannot be
 * edited through this endpoint.
 */
public record UpdateRecruiterProfileRequest(

        @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Phone number must be a valid phone number")
        String phoneNumber,

        @NotNull(message = "Title is required")
        RecruiterTitle title,

        @Size(max = 150, message = "Designation must not exceed 150 characters")
        String designation
) {
}

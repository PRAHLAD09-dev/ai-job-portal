package com.prahlad.aijobportal.candidateservice.candidate.dto.request;

import com.prahlad.aijobportal.candidateservice.candidate.enums.ProfileVisibility;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for {@code POST /candidate/profile}. Identity fields
 * (email, name) are NOT accepted here — they are fetched authoritatively
 * from Auth Service via Feign at creation time, so the profile can never
 * drift out of sync with the user's actual registered identity.
 */
public record CreateCandidateProfileRequest(

        @Size(max = 200, message = "Headline must not exceed 200 characters")
        String headline,

        @Size(max = 4000, message = "Summary must not exceed 4000 characters")
        String summary,

        @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Phone number must be a valid phone number")
        String phoneNumber,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,

        @Size(max = 500, message = "Portfolio URL must not exceed 500 characters")
        String portfolioUrl,

        @Size(max = 500, message = "LinkedIn URL must not exceed 500 characters")
        String linkedinUrl,

        @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
        String githubUrl,

        ProfileVisibility visibility
) {
}

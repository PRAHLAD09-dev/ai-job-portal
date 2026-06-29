package com.prahlad.aijobportal.candidateservice.experience.dto.request;

import com.prahlad.aijobportal.candidateservice.experience.enums.EmploymentType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExperienceRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must not exceed 200 characters")
        String companyName,

        @NotBlank(message = "Job title is required")
        @Size(max = 150, message = "Job title must not exceed 150 characters")
        String jobTitle,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @Size(max = 150, message = "Location must not exceed 150 characters")
        String location,

        @NotNull(message = "Start date is required")
        @PastOrPresent(message = "Start date cannot be in the future")
        LocalDate startDate,

        LocalDate endDate,

        boolean currentlyWorking,

        @Size(max = 4000, message = "Description must not exceed 4000 characters")
        String description
) {
    @AssertTrue(message = "End date must be provided when not currently working, and omitted when currently working")
    public boolean isEndDateConsistent() {
        return currentlyWorking ? endDate == null : endDate != null;
    }
}

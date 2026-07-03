package com.prahlad.aijobportal.aiservice.jobdescription.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record JobDescriptionRequest(

        @NotBlank(message = "Job title is required")
        @Size(max = 150, message = "Job title must not exceed 150 characters")
        String jobTitle,

        @NotBlank(message = "Job type is required")
        String jobType,

        @NotBlank(message = "Experience level is required")
        String experienceLevel,

        @NotEmpty(message = "At least one key responsibility or focus area is required")
        List<@NotBlank String> keyPoints
) {
}

package com.prahlad.aijobportal.jobservice.benefit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobBenefitRequest(

        @NotBlank(message = "Benefit title is required")
        @Size(max = 150, message = "Benefit title must not exceed 150 characters")
        String title,

        @Size(max = 500, message = "Benefit description must not exceed 500 characters")
        String description
) {
}

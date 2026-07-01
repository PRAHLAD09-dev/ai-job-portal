package com.prahlad.aijobportal.jobservice.requirement.dto.request;

import com.prahlad.aijobportal.jobservice.requirement.enums.RequirementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobRequirementRequest(

        @NotNull(message = "Requirement type is required")
        RequirementType type,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Min(value = 0, message = "Display order cannot be negative")
        int displayOrder
) {
}

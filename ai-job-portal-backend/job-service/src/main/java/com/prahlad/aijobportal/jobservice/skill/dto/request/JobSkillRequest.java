package com.prahlad.aijobportal.jobservice.skill.dto.request;

import com.prahlad.aijobportal.jobservice.skill.enums.RequiredProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobSkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Skill name must not exceed 100 characters")
        String name,

        @NotNull(message = "Required proficiency is required")
        RequiredProficiency requiredProficiency,

        boolean mandatory
) {
}

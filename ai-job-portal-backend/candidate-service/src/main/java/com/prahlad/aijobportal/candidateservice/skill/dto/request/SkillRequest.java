package com.prahlad.aijobportal.candidateservice.skill.dto.request;

import com.prahlad.aijobportal.candidateservice.skill.enums.SkillProficiency;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Skill name must not exceed 100 characters")
        String name,

        @NotNull(message = "Proficiency level is required")
        SkillProficiency proficiency,

        @Min(value = 0, message = "Years of experience cannot be negative")
        @Max(value = 60, message = "Years of experience must be realistic")
        Integer yearsOfExperience
) {
}

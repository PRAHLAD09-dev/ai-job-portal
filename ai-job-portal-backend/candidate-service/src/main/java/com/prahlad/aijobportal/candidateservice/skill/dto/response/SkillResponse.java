package com.prahlad.aijobportal.candidateservice.skill.dto.response;

import com.prahlad.aijobportal.candidateservice.skill.enums.SkillProficiency;

import java.util.UUID;

public record SkillResponse(
        UUID id,
        String name,
        SkillProficiency proficiency,
        Integer yearsOfExperience
) {
}

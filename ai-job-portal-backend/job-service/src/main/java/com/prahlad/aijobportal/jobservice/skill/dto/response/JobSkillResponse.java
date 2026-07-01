package com.prahlad.aijobportal.jobservice.skill.dto.response;

import com.prahlad.aijobportal.jobservice.skill.enums.RequiredProficiency;

import java.util.UUID;

public record JobSkillResponse(
        UUID id,
        String name,
        RequiredProficiency requiredProficiency,
        boolean mandatory
) {
}

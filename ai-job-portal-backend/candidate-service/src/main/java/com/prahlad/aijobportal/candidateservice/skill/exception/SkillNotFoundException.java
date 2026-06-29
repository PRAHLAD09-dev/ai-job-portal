package com.prahlad.aijobportal.candidateservice.skill.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class SkillNotFoundException extends ResourceNotFoundException {

    public SkillNotFoundException(UUID id) {
        super("Skill", "id", id);
    }
}

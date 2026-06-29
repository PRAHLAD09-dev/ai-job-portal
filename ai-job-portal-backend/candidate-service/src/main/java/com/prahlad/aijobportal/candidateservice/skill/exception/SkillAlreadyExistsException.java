package com.prahlad.aijobportal.candidateservice.skill.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a candidate attempts to add a skill they have already
 * listed (case-insensitive match on skill name).
 */
public class SkillAlreadyExistsException extends BusinessException {

    public SkillAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "SKILL_ALREADY_EXISTS");
    }
}

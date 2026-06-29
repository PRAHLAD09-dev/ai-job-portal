package com.prahlad.aijobportal.candidateservice.experience.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ExperienceNotFoundException extends ResourceNotFoundException {

    public ExperienceNotFoundException(UUID id) {
        super("Experience", "id", id);
    }
}

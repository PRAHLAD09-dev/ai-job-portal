package com.prahlad.aijobportal.candidateservice.education.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class EducationNotFoundException extends ResourceNotFoundException {

    public EducationNotFoundException(UUID id) {
        super("Education", "id", id);
    }
}

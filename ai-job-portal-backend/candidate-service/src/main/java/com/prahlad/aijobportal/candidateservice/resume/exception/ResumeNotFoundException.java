package com.prahlad.aijobportal.candidateservice.resume.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ResumeNotFoundException extends ResourceNotFoundException {

    public ResumeNotFoundException(UUID id) {
        super("Resume", "id", id);
    }
}

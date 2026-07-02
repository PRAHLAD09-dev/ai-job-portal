package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ApplicationNotFoundException extends ResourceNotFoundException {

    public ApplicationNotFoundException(UUID applicationId) {
        super("Application", "id", applicationId);
    }
}

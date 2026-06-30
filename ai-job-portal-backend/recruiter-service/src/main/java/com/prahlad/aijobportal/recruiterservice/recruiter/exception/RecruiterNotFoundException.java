package com.prahlad.aijobportal.recruiterservice.recruiter.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class RecruiterNotFoundException extends ResourceNotFoundException {

    private RecruiterNotFoundException(String message) {
        super(message);
    }

    public static RecruiterNotFoundException forUser(UUID userId) {
        return new RecruiterNotFoundException(
                "Recruiter profile not found for the authenticated user. Please create your company and profile first.");
    }
}

package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

public class ResumeNotFoundException extends ResourceNotFoundException {

    public ResumeNotFoundException() {
        super("No resume found on your candidate profile. Please upload a resume before applying.");
    }
}

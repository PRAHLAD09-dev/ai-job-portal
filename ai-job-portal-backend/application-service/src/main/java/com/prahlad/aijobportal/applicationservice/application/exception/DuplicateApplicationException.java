package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.ResourceConflictException;

public class DuplicateApplicationException extends ResourceConflictException {

    public DuplicateApplicationException() {
        super("You have already applied for this job");
    }
}

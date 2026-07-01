package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.ResourceConflictException;

public class SavedJobAlreadyExistsException extends ResourceConflictException {

    public SavedJobAlreadyExistsException() {
        super("This job has already been saved");
    }
}

package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class JobAlertNotFoundException extends ResourceNotFoundException {

    public JobAlertNotFoundException(UUID id) {
        super("JobAlert", "id", id);
    }
}

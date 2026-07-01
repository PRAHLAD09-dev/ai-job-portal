package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class JobCategoryNotFoundException extends ResourceNotFoundException {

    public JobCategoryNotFoundException(UUID id) {
        super("JobCategory", "id", id);
    }
}

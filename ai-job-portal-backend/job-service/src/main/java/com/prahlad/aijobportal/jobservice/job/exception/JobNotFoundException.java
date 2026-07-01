package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class JobNotFoundException extends ResourceNotFoundException {

    public JobNotFoundException(UUID id) {
        super("Job", "id", id);
    }

    public JobNotFoundException(String slug) {
        super("Job", "slug", slug);
    }
}

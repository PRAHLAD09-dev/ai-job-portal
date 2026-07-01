package com.prahlad.aijobportal.jobservice.job.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class SavedJobNotFoundException extends ResourceNotFoundException {

    public SavedJobNotFoundException(UUID jobId) {
        super("SavedJob", "jobId", jobId);
    }
}

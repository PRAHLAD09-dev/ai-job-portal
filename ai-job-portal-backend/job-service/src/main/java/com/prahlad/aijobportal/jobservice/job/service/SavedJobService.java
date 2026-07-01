package com.prahlad.aijobportal.jobservice.job.service;

import com.prahlad.aijobportal.jobservice.job.dto.response.SavedJobResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SavedJobService {

    SavedJobResponse saveJob(UUID userId, UUID jobId);

    void unsaveJob(UUID userId, UUID jobId);

    PageResponse<SavedJobResponse> getMySavedJobs(UUID userId, Pageable pageable);
}

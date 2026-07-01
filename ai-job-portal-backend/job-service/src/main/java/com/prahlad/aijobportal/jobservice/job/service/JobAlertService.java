package com.prahlad.aijobportal.jobservice.job.service;

import com.prahlad.aijobportal.jobservice.job.dto.request.JobAlertRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobAlertResponse;

import java.util.List;
import java.util.UUID;

public interface JobAlertService {

    JobAlertResponse createAlert(UUID userId, JobAlertRequest request);

    JobAlertResponse updateAlert(UUID userId, UUID alertId, JobAlertRequest request);

    void deleteAlert(UUID userId, UUID alertId);

    List<JobAlertResponse> getMyAlerts(UUID userId);
}

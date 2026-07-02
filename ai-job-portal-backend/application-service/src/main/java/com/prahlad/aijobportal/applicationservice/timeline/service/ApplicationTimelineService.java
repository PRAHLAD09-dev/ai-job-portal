package com.prahlad.aijobportal.applicationservice.timeline.service;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.timeline.dto.response.TimelineResponse;

import java.util.List;
import java.util.UUID;

public interface ApplicationTimelineService {

    void recordTransition(JobApplication application, ApplicationStatus oldStatus, ApplicationStatus newStatus,
                           UUID changedBy, String remarks);

    List<TimelineResponse> getTimeline(UUID applicationId);
}

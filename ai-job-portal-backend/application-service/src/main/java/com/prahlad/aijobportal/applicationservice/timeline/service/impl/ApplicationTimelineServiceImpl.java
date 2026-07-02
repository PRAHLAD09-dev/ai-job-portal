package com.prahlad.aijobportal.applicationservice.timeline.service.impl;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.timeline.dto.response.TimelineResponse;
import com.prahlad.aijobportal.applicationservice.timeline.entity.ApplicationTimeline;
import com.prahlad.aijobportal.applicationservice.timeline.mapper.ApplicationTimelineMapper;
import com.prahlad.aijobportal.applicationservice.timeline.repository.ApplicationTimelineRepository;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationTimelineServiceImpl implements ApplicationTimelineService {

    private final ApplicationTimelineRepository timelineRepository;
    private final ApplicationTimelineMapper timelineMapper;

    @Override
    @Transactional
    public void recordTransition(JobApplication application, ApplicationStatus oldStatus, ApplicationStatus newStatus,
                                  UUID changedBy, String remarks) {
        ApplicationTimeline timeline = ApplicationTimeline.builder()
                .application(application)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .remarks(remarks)
                .build();
        timelineRepository.save(timeline);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineResponse> getTimeline(UUID applicationId) {
        return timelineRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId).stream()
                .map(timelineMapper::toResponse)
                .toList();
    }
}

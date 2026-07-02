package com.prahlad.aijobportal.applicationservice.timeline.repository;

import com.prahlad.aijobportal.applicationservice.timeline.entity.ApplicationTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationTimelineRepository extends JpaRepository<ApplicationTimeline, UUID> {

    List<ApplicationTimeline> findByApplicationIdOrderByCreatedAtAsc(UUID applicationId);
}

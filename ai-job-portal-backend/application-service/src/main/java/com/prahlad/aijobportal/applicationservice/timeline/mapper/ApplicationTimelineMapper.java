package com.prahlad.aijobportal.applicationservice.timeline.mapper;

import com.prahlad.aijobportal.applicationservice.timeline.dto.response.TimelineResponse;
import com.prahlad.aijobportal.applicationservice.timeline.entity.ApplicationTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationTimelineMapper {

    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "changedAt", source = "createdAt")
    TimelineResponse toResponse(ApplicationTimeline timeline);
}

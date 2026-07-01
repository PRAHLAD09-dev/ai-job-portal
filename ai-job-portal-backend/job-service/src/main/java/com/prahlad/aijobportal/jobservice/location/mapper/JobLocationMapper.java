package com.prahlad.aijobportal.jobservice.location.mapper;

import com.prahlad.aijobportal.jobservice.location.dto.request.JobLocationRequest;
import com.prahlad.aijobportal.jobservice.location.dto.response.JobLocationResponse;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobLocationMapper {

    JobLocation toEntity(JobLocationRequest request);

    JobLocationResponse toResponse(JobLocation jobLocation);
}

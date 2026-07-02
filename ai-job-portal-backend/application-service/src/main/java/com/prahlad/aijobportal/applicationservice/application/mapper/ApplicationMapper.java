package com.prahlad.aijobportal.applicationservice.application.mapper;

import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationResponse toResponse(JobApplication application);

    ApplicationSummaryResponse toSummaryResponse(JobApplication application);
}

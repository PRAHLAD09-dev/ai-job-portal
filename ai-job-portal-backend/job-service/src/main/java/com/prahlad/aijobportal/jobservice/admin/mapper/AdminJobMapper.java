package com.prahlad.aijobportal.jobservice.admin.mapper;

import com.prahlad.aijobportal.jobservice.admin.dto.response.AdminJobResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminJobMapper {

    AdminJobResponse toResponse(Job job);
}

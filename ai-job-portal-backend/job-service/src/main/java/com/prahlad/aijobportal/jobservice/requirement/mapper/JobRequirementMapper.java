package com.prahlad.aijobportal.jobservice.requirement.mapper;

import com.prahlad.aijobportal.jobservice.requirement.dto.request.JobRequirementRequest;
import com.prahlad.aijobportal.jobservice.requirement.dto.response.JobRequirementResponse;
import com.prahlad.aijobportal.jobservice.requirement.entity.JobRequirement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobRequirementMapper {

    JobRequirement toEntity(JobRequirementRequest request);

    JobRequirementResponse toResponse(JobRequirement jobRequirement);
}

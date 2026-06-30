package com.prahlad.aijobportal.recruiterservice.recruiter.mapper;

import com.prahlad.aijobportal.recruiterservice.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.prahlad.aijobportal.recruiterservice.recruiter.dto.response.RecruiterResponse;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecruiterMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    RecruiterResponse toResponse(Recruiter recruiter);

    void updateEntityFromRequest(UpdateRecruiterProfileRequest request, @MappingTarget Recruiter recruiter);
}

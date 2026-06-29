package com.prahlad.aijobportal.candidateservice.education.mapper;

import com.prahlad.aijobportal.candidateservice.education.dto.request.EducationRequest;
import com.prahlad.aijobportal.candidateservice.education.dto.response.EducationResponse;
import com.prahlad.aijobportal.candidateservice.education.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationMapper {

    Education toEntity(EducationRequest request);

    EducationResponse toResponse(Education education);

    void updateEntityFromRequest(EducationRequest request, @MappingTarget Education education);
}

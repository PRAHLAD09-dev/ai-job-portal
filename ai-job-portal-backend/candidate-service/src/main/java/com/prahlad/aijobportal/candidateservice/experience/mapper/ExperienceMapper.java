package com.prahlad.aijobportal.candidateservice.experience.mapper;

import com.prahlad.aijobportal.candidateservice.experience.dto.request.ExperienceRequest;
import com.prahlad.aijobportal.candidateservice.experience.dto.response.ExperienceResponse;
import com.prahlad.aijobportal.candidateservice.experience.entity.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExperienceMapper {

    Experience toEntity(ExperienceRequest request);

    ExperienceResponse toResponse(Experience experience);

    void updateEntityFromRequest(ExperienceRequest request, @MappingTarget Experience experience);
}

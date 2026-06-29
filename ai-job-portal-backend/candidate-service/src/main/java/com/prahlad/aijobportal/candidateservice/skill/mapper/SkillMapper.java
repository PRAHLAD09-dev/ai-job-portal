package com.prahlad.aijobportal.candidateservice.skill.mapper;

import com.prahlad.aijobportal.candidateservice.skill.dto.request.SkillRequest;
import com.prahlad.aijobportal.candidateservice.skill.dto.response.SkillResponse;
import com.prahlad.aijobportal.candidateservice.skill.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SkillMapper {

    Skill toEntity(SkillRequest request);

    SkillResponse toResponse(Skill skill);

    void updateEntityFromRequest(SkillRequest request, @MappingTarget Skill skill);
}

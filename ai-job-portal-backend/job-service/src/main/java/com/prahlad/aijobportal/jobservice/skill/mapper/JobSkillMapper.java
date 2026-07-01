package com.prahlad.aijobportal.jobservice.skill.mapper;

import com.prahlad.aijobportal.jobservice.skill.dto.request.JobSkillRequest;
import com.prahlad.aijobportal.jobservice.skill.dto.response.JobSkillResponse;
import com.prahlad.aijobportal.jobservice.skill.entity.JobSkill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobSkillMapper {

    JobSkill toEntity(JobSkillRequest request);

    JobSkillResponse toResponse(JobSkill jobSkill);
}

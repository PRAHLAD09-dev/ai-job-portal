package com.prahlad.aijobportal.candidateservice.candidate.mapper;

import com.prahlad.aijobportal.candidateservice.candidate.dto.request.UpdateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.CandidateProfileResponse;
import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.education.mapper.EducationMapper;
import com.prahlad.aijobportal.candidateservice.experience.mapper.ExperienceMapper;
import com.prahlad.aijobportal.candidateservice.resume.mapper.ResumeMapper;
import com.prahlad.aijobportal.candidateservice.skill.mapper.SkillMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {EducationMapper.class, ExperienceMapper.class, SkillMapper.class, ResumeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CandidateMapper {

    CandidateProfileResponse toResponse(Candidate candidate);

    void updateEntityFromRequest(UpdateCandidateProfileRequest request, @MappingTarget Candidate candidate);
}

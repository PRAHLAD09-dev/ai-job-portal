package com.prahlad.aijobportal.candidateservice.resume.mapper;

import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import com.prahlad.aijobportal.candidateservice.resume.entity.Resume;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    ResumeResponse toResponse(Resume resume);
}

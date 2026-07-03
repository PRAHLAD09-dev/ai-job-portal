package com.prahlad.aijobportal.aiservice.interview.mapper;

import com.prahlad.aijobportal.aiservice.interview.dto.response.InterviewQuestionResponse;
import com.prahlad.aijobportal.aiservice.interview.entity.InterviewQuestion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterviewQuestionMapper {

    InterviewQuestionResponse toResponse(InterviewQuestion entity);
}

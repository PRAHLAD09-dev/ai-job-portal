package com.prahlad.aijobportal.jobservice.category.mapper;

import com.prahlad.aijobportal.jobservice.category.dto.response.JobCategoryResponse;
import com.prahlad.aijobportal.jobservice.category.entity.JobCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobCategoryMapper {

    JobCategoryResponse toResponse(JobCategory category);
}

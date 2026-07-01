package com.prahlad.aijobportal.jobservice.job.mapper;

import com.prahlad.aijobportal.jobservice.benefit.mapper.JobBenefitMapper;
import com.prahlad.aijobportal.jobservice.category.mapper.JobCategoryMapper;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import com.prahlad.aijobportal.jobservice.location.mapper.JobLocationMapper;
import com.prahlad.aijobportal.jobservice.requirement.mapper.JobRequirementMapper;
import com.prahlad.aijobportal.jobservice.skill.mapper.JobSkillMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {JobCategoryMapper.class, JobSkillMapper.class, JobBenefitMapper.class,
                JobLocationMapper.class, JobRequirementMapper.class}
)
public interface JobMapper {

    JobResponse toResponse(Job job);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "cities", expression = "java(extractCities(job.getLocations()))")
    JobSummaryResponse toSummaryResponse(Job job);

    default List<String> extractCities(List<JobLocation> locations) {
        if (locations == null) {
            return List.of();
        }
        return locations.stream().map(JobLocation::getCity).toList();
    }
}

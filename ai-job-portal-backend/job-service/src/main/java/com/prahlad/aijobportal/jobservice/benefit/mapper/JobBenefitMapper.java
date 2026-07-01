package com.prahlad.aijobportal.jobservice.benefit.mapper;

import com.prahlad.aijobportal.jobservice.benefit.dto.request.JobBenefitRequest;
import com.prahlad.aijobportal.jobservice.benefit.dto.response.JobBenefitResponse;
import com.prahlad.aijobportal.jobservice.benefit.entity.JobBenefit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobBenefitMapper {

    JobBenefit toEntity(JobBenefitRequest request);

    JobBenefitResponse toResponse(JobBenefit jobBenefit);
}

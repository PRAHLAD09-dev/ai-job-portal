package com.prahlad.aijobportal.recruiterservice.location.mapper;

import com.prahlad.aijobportal.recruiterservice.location.dto.request.CompanyLocationRequest;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;
import com.prahlad.aijobportal.recruiterservice.location.entity.CompanyLocation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyLocationMapper {

    CompanyLocation toEntity(CompanyLocationRequest request);

    CompanyLocationResponse toResponse(CompanyLocation location);

    void updateEntityFromRequest(CompanyLocationRequest request, @MappingTarget CompanyLocation location);
}

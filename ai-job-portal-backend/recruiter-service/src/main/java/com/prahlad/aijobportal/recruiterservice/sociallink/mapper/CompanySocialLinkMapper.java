package com.prahlad.aijobportal.recruiterservice.sociallink.mapper;

import com.prahlad.aijobportal.recruiterservice.sociallink.dto.request.CompanySocialLinkRequest;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;
import com.prahlad.aijobportal.recruiterservice.sociallink.entity.CompanySocialLink;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanySocialLinkMapper {

    CompanySocialLink toEntity(CompanySocialLinkRequest request);

    CompanySocialLinkResponse toResponse(CompanySocialLink link);

    void updateEntityFromRequest(CompanySocialLinkRequest request, @MappingTarget CompanySocialLink link);
}

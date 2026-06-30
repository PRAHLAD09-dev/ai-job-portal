package com.prahlad.aijobportal.recruiterservice.company.mapper;

import com.prahlad.aijobportal.recruiterservice.company.dto.request.UpdateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyPublicResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.location.mapper.CompanyLocationMapper;
import com.prahlad.aijobportal.recruiterservice.sociallink.mapper.CompanySocialLinkMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {CompanyLocationMapper.class, CompanySocialLinkMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    CompanyPublicResponse toPublicResponse(Company company);

    void updateEntityFromRequest(UpdateCompanyRequest request, @MappingTarget Company company);
}

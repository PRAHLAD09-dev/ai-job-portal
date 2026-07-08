package com.prahlad.aijobportal.recruiterservice.admin.mapper;

import com.prahlad.aijobportal.recruiterservice.admin.dto.response.AdminCompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCompanyMapper {

    AdminCompanyResponse toResponse(Company company);
}

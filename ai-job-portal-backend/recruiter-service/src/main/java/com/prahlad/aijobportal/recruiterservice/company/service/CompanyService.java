package com.prahlad.aijobportal.recruiterservice.company.service;

import com.prahlad.aijobportal.recruiterservice.company.dto.request.CreateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.request.UpdateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyPublicResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyStatisticsResponse;

import java.util.UUID;

public interface CompanyService {

    CompanyResponse createCompany(UUID userId, String bearerToken, CreateCompanyRequest request);

    CompanyResponse getMyCompany(UUID userId);

    CompanyResponse updateMyCompany(UUID userId, UpdateCompanyRequest request);

    void deleteMyCompany(UUID userId);

    CompanyPublicResponse getPublicProfile(String slug);

    CompanyStatisticsResponse getMyCompanyStatistics(UUID userId);
}

package com.prahlad.aijobportal.recruiterservice.location.service;

import com.prahlad.aijobportal.recruiterservice.location.dto.request.CompanyLocationRequest;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyLocationService {

    CompanyLocationResponse create(UUID userId, CompanyLocationRequest request);

    List<CompanyLocationResponse> getAll(UUID userId);

    CompanyLocationResponse update(UUID userId, UUID locationId, CompanyLocationRequest request);

    void delete(UUID userId, UUID locationId);
}

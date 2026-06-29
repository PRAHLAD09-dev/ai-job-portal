package com.prahlad.aijobportal.candidateservice.education.service;

import com.prahlad.aijobportal.candidateservice.education.dto.request.EducationRequest;
import com.prahlad.aijobportal.candidateservice.education.dto.response.EducationResponse;

import java.util.List;
import java.util.UUID;

public interface EducationService {

    EducationResponse create(UUID userId, EducationRequest request);

    List<EducationResponse> getAll(UUID userId);

    EducationResponse update(UUID userId, UUID educationId, EducationRequest request);

    void delete(UUID userId, UUID educationId);
}

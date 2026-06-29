package com.prahlad.aijobportal.candidateservice.experience.service;

import com.prahlad.aijobportal.candidateservice.experience.dto.request.ExperienceRequest;
import com.prahlad.aijobportal.candidateservice.experience.dto.response.ExperienceResponse;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {

    ExperienceResponse create(UUID userId, ExperienceRequest request);

    List<ExperienceResponse> getAll(UUID userId);

    ExperienceResponse update(UUID userId, UUID experienceId, ExperienceRequest request);

    void delete(UUID userId, UUID experienceId);
}

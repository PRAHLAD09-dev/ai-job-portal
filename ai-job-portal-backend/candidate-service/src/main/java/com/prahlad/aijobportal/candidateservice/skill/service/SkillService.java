package com.prahlad.aijobportal.candidateservice.skill.service;

import com.prahlad.aijobportal.candidateservice.skill.dto.request.SkillRequest;
import com.prahlad.aijobportal.candidateservice.skill.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {

    SkillResponse create(UUID userId, SkillRequest request);

    List<SkillResponse> getAll(UUID userId);

    SkillResponse update(UUID userId, UUID skillId, SkillRequest request);

    void delete(UUID userId, UUID skillId);
}

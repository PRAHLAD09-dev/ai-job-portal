package com.prahlad.aijobportal.candidateservice.experience.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.experience.dto.request.ExperienceRequest;
import com.prahlad.aijobportal.candidateservice.experience.dto.response.ExperienceResponse;
import com.prahlad.aijobportal.candidateservice.experience.entity.Experience;
import com.prahlad.aijobportal.candidateservice.experience.exception.ExperienceNotFoundException;
import com.prahlad.aijobportal.candidateservice.experience.mapper.ExperienceMapper;
import com.prahlad.aijobportal.candidateservice.experience.repository.ExperienceRepository;
import com.prahlad.aijobportal.candidateservice.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;
    private final CandidateLookupService candidateLookupService;
    private final ProfileCompletionService profileCompletionService;

    @Override
    @Transactional
    public ExperienceResponse create(UUID userId, ExperienceRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Experience experience = experienceMapper.toEntity(request);
        experience.setCandidate(candidate);

        Experience saved = experienceRepository.save(experience);
        profileCompletionService.recalculate(candidate);

        log.info("Created experience entry id={} for candidateId={}", saved.getId(), candidate.getId());
        return experienceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> getAll(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return experienceRepository.findByCandidateId(candidate.getId()).stream()
                .map(experienceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ExperienceResponse update(UUID userId, UUID experienceId, ExperienceRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Experience experience = experienceRepository.findByIdAndCandidateId(experienceId, candidate.getId())
                .orElseThrow(() -> new ExperienceNotFoundException(experienceId));

        experienceMapper.updateEntityFromRequest(request, experience);
        Experience saved = experienceRepository.save(experience);

        log.info("Updated experience entry id={} for candidateId={}", saved.getId(), candidate.getId());
        return experienceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID experienceId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Experience experience = experienceRepository.findByIdAndCandidateId(experienceId, candidate.getId())
                .orElseThrow(() -> new ExperienceNotFoundException(experienceId));

        experienceRepository.delete(experience);
        profileCompletionService.recalculate(candidate);

        log.info("Deleted experience entry id={} for candidateId={}", experienceId, candidate.getId());
    }
}

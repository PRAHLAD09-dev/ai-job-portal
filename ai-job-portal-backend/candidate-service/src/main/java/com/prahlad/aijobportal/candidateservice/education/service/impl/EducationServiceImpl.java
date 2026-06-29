package com.prahlad.aijobportal.candidateservice.education.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.education.dto.request.EducationRequest;
import com.prahlad.aijobportal.candidateservice.education.dto.response.EducationResponse;
import com.prahlad.aijobportal.candidateservice.education.entity.Education;
import com.prahlad.aijobportal.candidateservice.education.exception.EducationNotFoundException;
import com.prahlad.aijobportal.candidateservice.education.mapper.EducationMapper;
import com.prahlad.aijobportal.candidateservice.education.repository.EducationRepository;
import com.prahlad.aijobportal.candidateservice.education.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;
    private final CandidateLookupService candidateLookupService;
    private final ProfileCompletionService profileCompletionService;

    @Override
    @Transactional
    public EducationResponse create(UUID userId, EducationRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Education education = educationMapper.toEntity(request);
        education.setCandidate(candidate);

        Education saved = educationRepository.save(education);
        profileCompletionService.recalculate(candidate);

        log.info("Created education entry id={} for candidateId={}", saved.getId(), candidate.getId());
        return educationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getAll(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return educationRepository.findByCandidateId(candidate.getId()).stream()
                .map(educationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EducationResponse update(UUID userId, UUID educationId, EducationRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Education education = educationRepository.findByIdAndCandidateId(educationId, candidate.getId())
                .orElseThrow(() -> new EducationNotFoundException(educationId));

        educationMapper.updateEntityFromRequest(request, education);
        Education saved = educationRepository.save(education);

        log.info("Updated education entry id={} for candidateId={}", saved.getId(), candidate.getId());
        return educationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID educationId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Education education = educationRepository.findByIdAndCandidateId(educationId, candidate.getId())
                .orElseThrow(() -> new EducationNotFoundException(educationId));

        educationRepository.delete(education);
        profileCompletionService.recalculate(candidate);

        log.info("Deleted education entry id={} for candidateId={}", educationId, candidate.getId());
    }
}

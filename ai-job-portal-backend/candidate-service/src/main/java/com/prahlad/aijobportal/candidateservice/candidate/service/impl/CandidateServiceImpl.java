package com.prahlad.aijobportal.candidateservice.candidate.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.dto.request.CreateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.request.UpdateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.CandidateProfileResponse;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.ProfileCompletionResponse;
import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.enums.ProfileVisibility;
import com.prahlad.aijobportal.candidateservice.candidate.exception.CandidateProfileAlreadyExistsException;
import com.prahlad.aijobportal.candidateservice.candidate.mapper.CandidateMapper;
import com.prahlad.aijobportal.candidateservice.candidate.repository.CandidateRepository;
import com.prahlad.aijobportal.candidateservice.candidate.service.AuthUserLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.feign.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateLookupService candidateLookupService;
    private final ProfileCompletionService profileCompletionService;
    private final AuthUserLookupService authUserLookupService;

    @Override
    @Transactional
    public CandidateProfileResponse createProfile(UUID userId, String bearerToken,
            CreateCandidateProfileRequest request) {
        if (candidateRepository.existsByUserId(userId)) {
            throw new CandidateProfileAlreadyExistsException("A candidate profile already exists for this account");
        }

        UserSummaryResponse authUser = authUserLookupService.fetchCurrentUser(bearerToken);

        Candidate candidate = Candidate.builder()
                .userId(userId)
                .email(authUser.email())
                .fullName(authUser.firstName() + " " + authUser.lastName())
                .headline(request.headline())
                .summary(request.summary())
                .phoneNumber(request.phoneNumber())
                .dateOfBirth(request.dateOfBirth())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .portfolioUrl(request.portfolioUrl())
                .linkedinUrl(request.linkedinUrl())
                .githubUrl(request.githubUrl())
                .visibility(request.visibility() != null ? request.visibility() : ProfileVisibility.PUBLIC)
                .build();

        Candidate saved = candidateRepository.save(candidate);
        profileCompletionService.recalculate(saved);

        log.info("Created candidate profile id={} for userId={}", saved.getId(), userId);
        return candidateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfile(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return candidateMapper.toResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(UUID userId, UpdateCandidateProfileRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        candidateMapper.updateEntityFromRequest(request, candidate);
        Candidate saved = candidateRepository.save(candidate);
        profileCompletionService.recalculate(saved);

        log.info("Updated candidate profile id={} for userId={}", saved.getId(), userId);
        return candidateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProfile(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        candidateRepository.delete(candidate);
        log.info("Deleted candidate profile id={} for userId={}", candidate.getId(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionResponse getProfileCompletion(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return new ProfileCompletionResponse(candidate.getProfileCompletionPercentage());
    }
}

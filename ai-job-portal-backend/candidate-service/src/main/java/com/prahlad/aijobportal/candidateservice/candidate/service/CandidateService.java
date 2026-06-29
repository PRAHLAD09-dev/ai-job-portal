package com.prahlad.aijobportal.candidateservice.candidate.service;

import com.prahlad.aijobportal.candidateservice.candidate.dto.request.CreateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.request.UpdateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.CandidateProfileResponse;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.ProfileCompletionResponse;

import java.util.UUID;

public interface CandidateService {

    CandidateProfileResponse createProfile(UUID userId, String bearerToken, CreateCandidateProfileRequest request);

    CandidateProfileResponse getProfile(UUID userId);

    CandidateProfileResponse updateProfile(UUID userId, UpdateCandidateProfileRequest request);

    void deleteProfile(UUID userId);

    ProfileCompletionResponse getProfileCompletion(UUID userId);
}

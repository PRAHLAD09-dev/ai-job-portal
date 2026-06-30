package com.prahlad.aijobportal.recruiterservice.recruiter.service;

import com.prahlad.aijobportal.recruiterservice.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.prahlad.aijobportal.recruiterservice.recruiter.dto.response.RecruiterResponse;

import java.util.UUID;

public interface RecruiterService {

    RecruiterResponse getMyProfile(UUID userId);

    RecruiterResponse updateMyProfile(UUID userId, UpdateRecruiterProfileRequest request);
}

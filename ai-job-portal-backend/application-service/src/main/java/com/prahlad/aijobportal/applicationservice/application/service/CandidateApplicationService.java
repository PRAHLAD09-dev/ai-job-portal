package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.dto.request.CreateApplicationRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CandidateApplicationService {

    ApplicationResponse apply(UUID candidateUserId, String bearerToken, CreateApplicationRequest request);

    void withdraw(UUID candidateUserId, UUID applicationId);

    ApplicationResponse getApplicationDetail(UUID candidateUserId, UUID applicationId);

    PageResponse<ApplicationSummaryResponse> getMyApplications(UUID candidateUserId, Pageable pageable);
}

package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.dto.request.ApplicationSearchCriteria;
import com.prahlad.aijobportal.applicationservice.application.dto.request.RecruiterNotesRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.request.UpdateApplicationStatusRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RecruiterApplicationService {

    PageResponse<ApplicationSummaryResponse> getApplications(UUID recruiterUserId, String bearerToken,
                                                               ApplicationSearchCriteria criteria, Pageable pageable);

    ApplicationResponse getApplicationDetail(UUID recruiterUserId, String bearerToken, UUID applicationId);

    ApplicationResponse updateStatus(UUID recruiterUserId, String bearerToken, UUID applicationId,
                                      UpdateApplicationStatusRequest request);

    ApplicationResponse addNotes(UUID recruiterUserId, String bearerToken, UUID applicationId,
                                  RecruiterNotesRequest request);

    ApplicationStatisticsResponse getStatistics(UUID recruiterUserId, String bearerToken);
}

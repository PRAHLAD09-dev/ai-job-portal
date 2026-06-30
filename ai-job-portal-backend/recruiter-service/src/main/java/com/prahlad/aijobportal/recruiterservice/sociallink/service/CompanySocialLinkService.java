package com.prahlad.aijobportal.recruiterservice.sociallink.service;

import com.prahlad.aijobportal.recruiterservice.sociallink.dto.request.CompanySocialLinkRequest;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;

import java.util.List;
import java.util.UUID;

public interface CompanySocialLinkService {

    CompanySocialLinkResponse create(UUID userId, CompanySocialLinkRequest request);

    List<CompanySocialLinkResponse> getAll(UUID userId);

    CompanySocialLinkResponse update(UUID userId, UUID linkId, CompanySocialLinkRequest request);

    void delete(UUID userId, UUID linkId);
}

package com.prahlad.aijobportal.recruiterservice.recruiter.service.impl;

import com.prahlad.aijobportal.recruiterservice.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.prahlad.aijobportal.recruiterservice.recruiter.dto.response.RecruiterResponse;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.recruiter.mapper.RecruiterMapper;
import com.prahlad.aijobportal.recruiterservice.recruiter.repository.RecruiterRepository;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.RecruiterLookupService;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterServiceImpl implements RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final RecruiterMapper recruiterMapper;
    private final RecruiterLookupService recruiterLookupService;

    @Override
    @Transactional(readOnly = true)
    public RecruiterResponse getMyProfile(UUID userId) {
        Recruiter recruiter = recruiterLookupService.getByUserIdOrThrow(userId);
        return recruiterMapper.toResponse(recruiter);
    }

    @Override
    @Transactional
    public RecruiterResponse updateMyProfile(UUID userId, UpdateRecruiterProfileRequest request) {
        Recruiter recruiter = recruiterLookupService.getByUserIdOrThrow(userId);

        recruiterMapper.updateEntityFromRequest(request, recruiter);
        Recruiter saved = recruiterRepository.save(recruiter);

        log.info("Updated recruiter profile id={} for userId={}", saved.getId(), userId);
        return recruiterMapper.toResponse(saved);
    }
}

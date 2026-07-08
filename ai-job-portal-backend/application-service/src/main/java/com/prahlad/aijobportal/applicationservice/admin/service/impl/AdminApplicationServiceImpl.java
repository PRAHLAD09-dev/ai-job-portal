package com.prahlad.aijobportal.applicationservice.admin.service.impl;

import com.prahlad.aijobportal.applicationservice.admin.dto.response.ApplicationPlatformStatisticsResponse;
import com.prahlad.aijobportal.applicationservice.admin.service.AdminApplicationService;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public ApplicationPlatformStatisticsResponse getPlatformStatistics() {
        return new ApplicationPlatformStatisticsResponse(
                jobApplicationRepository.count(),
                jobApplicationRepository.countByStatus(ApplicationStatus.APPLIED),
                jobApplicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW),
                jobApplicationRepository.countByStatus(ApplicationStatus.SHORTLISTED),
                jobApplicationRepository.countByStatus(ApplicationStatus.INTERVIEW),
                jobApplicationRepository.countByStatus(ApplicationStatus.OFFERED),
                jobApplicationRepository.countByStatus(ApplicationStatus.HIRED),
                jobApplicationRepository.countByStatus(ApplicationStatus.REJECTED),
                jobApplicationRepository.countByStatus(ApplicationStatus.WITHDRAWN)
        );
    }
}

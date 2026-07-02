package com.prahlad.aijobportal.applicationservice.application.service.impl;

import com.prahlad.aijobportal.applicationservice.application.dto.request.ApplicationSearchCriteria;
import com.prahlad.aijobportal.applicationservice.application.dto.request.RecruiterNotesRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.request.UpdateApplicationStatusRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.exception.InvalidApplicationStateException;
import com.prahlad.aijobportal.applicationservice.application.mapper.ApplicationMapper;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import com.prahlad.aijobportal.applicationservice.application.service.ApplicationOwnershipGuard;
import com.prahlad.aijobportal.applicationservice.application.service.ApplicationService;
import com.prahlad.aijobportal.applicationservice.application.service.RecruiterApplicationService;
import com.prahlad.aijobportal.applicationservice.application.service.RecruiterLookupService;
import com.prahlad.aijobportal.applicationservice.application.specification.ApplicationSpecification;
import com.prahlad.aijobportal.applicationservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.applicationservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecruiterApplicationServiceImpl implements RecruiterApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationService applicationService;
    private final ApplicationOwnershipGuard applicationOwnershipGuard;
    private final RecruiterLookupService recruiterLookupService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationSummaryResponse> getApplications(UUID recruiterUserId, String bearerToken,
                                                                      ApplicationSearchCriteria criteria, Pageable pageable) {
        UUID companyId = resolveCompanyId(bearerToken);
        Page<ApplicationSummaryResponse> page = applicationRepository
                .findAll(ApplicationSpecification.forCompany(companyId, criteria), pageable)
                .map(applicationMapper::toSummaryResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationDetail(UUID recruiterUserId, String bearerToken, UUID applicationId) {
        UUID companyId = resolveCompanyId(bearerToken);
        JobApplication application = applicationOwnershipGuard.getOwnedApplicationOrThrow(applicationId, companyId);
        return applicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.RECRUITER_DASHBOARD_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.APPLICATION_STATISTICS_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.RECENT_APPLICATIONS_CACHE, allEntries = true)
    })
    public ApplicationResponse updateStatus(UUID recruiterUserId, String bearerToken, UUID applicationId,
                                             UpdateApplicationStatusRequest request) {
        RecruiterSummaryResponse recruiter = recruiterLookupService.fetchCurrentRecruiter(bearerToken);
        JobApplication application = applicationOwnershipGuard.getOwnedApplicationOrThrow(applicationId, recruiter.companyId());

        if (request.status() == ApplicationStatus.INTERVIEW && request.interviewDate() == null) {
            throw new InvalidApplicationStateException("An interview date is required to move an application to INTERVIEW");
        }

        application.setRecruiterId(recruiter.id());
        application.setRecruiterUserId(recruiterUserId);

        JobApplication updated = applicationService.transitionStatus(application, request.status(), recruiterUserId,
                request.remarks(), request.interviewDate());

        return applicationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.RECRUITER_DASHBOARD_CACHE, allEntries = true)
    public ApplicationResponse addNotes(UUID recruiterUserId, String bearerToken, UUID applicationId,
                                         RecruiterNotesRequest request) {
        RecruiterSummaryResponse recruiter = recruiterLookupService.fetchCurrentRecruiter(bearerToken);
        JobApplication application = applicationOwnershipGuard.getOwnedApplicationOrThrow(applicationId, recruiter.companyId());

        application.setNotes(request.notes());
        application.setRecruiterId(recruiter.id());
        application.setRecruiterUserId(recruiterUserId);

        JobApplication saved = applicationRepository.save(application);
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationStatisticsResponse getStatistics(UUID recruiterUserId, String bearerToken) {
        UUID companyId = resolveCompanyId(bearerToken);
        return computeStatistics(companyId);
    }

    @Cacheable(value = RedisCacheConfig.APPLICATION_STATISTICS_CACHE, key = "#companyId")
    public ApplicationStatisticsResponse computeStatistics(UUID companyId) {
        Map<String, Long> countByStatus = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            countByStatus.put(status.name(), applicationRepository.countByCompanyIdAndStatus(companyId, status));
        }

        long total = applicationRepository.countByCompanyId(companyId);
        return new ApplicationStatisticsResponse(companyId, total, countByStatus);
    }

    private UUID resolveCompanyId(String bearerToken) {
        return recruiterLookupService.fetchCurrentRecruiter(bearerToken).companyId();
    }
}

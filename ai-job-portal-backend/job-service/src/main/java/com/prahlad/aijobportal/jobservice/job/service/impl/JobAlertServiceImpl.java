package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.job.dto.request.JobAlertRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobAlertResponse;
import com.prahlad.aijobportal.jobservice.job.entity.JobAlert;
import com.prahlad.aijobportal.jobservice.job.exception.JobAlertNotFoundException;
import com.prahlad.aijobportal.jobservice.job.repository.JobAlertRepository;
import com.prahlad.aijobportal.jobservice.job.service.JobAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAlertServiceImpl implements JobAlertService {

    private final JobAlertRepository jobAlertRepository;

    @Override
    @Transactional
    public JobAlertResponse createAlert(UUID userId, JobAlertRequest request) {
        JobAlert alert = JobAlert.builder()
                .userId(userId)
                .keyword(request.keyword())
                .categoryId(request.categoryId())
                .jobType(request.jobType())
                .experienceLevel(request.experienceLevel())
                .workMode(request.workMode())
                .city(request.city())
                .frequency(request.frequency())
                .active(true)
                .build();

        JobAlert saved = jobAlertRepository.save(alert);
        log.info("Created job alert id={} for userId={}", saved.getId(), userId);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public JobAlertResponse updateAlert(UUID userId, UUID alertId, JobAlertRequest request) {
        JobAlert alert = jobAlertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new JobAlertNotFoundException(alertId));

        alert.setKeyword(request.keyword());
        alert.setCategoryId(request.categoryId());
        alert.setJobType(request.jobType());
        alert.setExperienceLevel(request.experienceLevel());
        alert.setWorkMode(request.workMode());
        alert.setCity(request.city());
        alert.setFrequency(request.frequency());

        JobAlert saved = jobAlertRepository.save(alert);
        log.info("Updated job alert id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAlert(UUID userId, UUID alertId) {
        JobAlert alert = jobAlertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new JobAlertNotFoundException(alertId));
        jobAlertRepository.delete(alert);
        log.info("Deleted job alert id={}", alertId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobAlertResponse> getMyAlerts(UUID userId) {
        return jobAlertRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private JobAlertResponse toResponse(JobAlert alert) {
        return new JobAlertResponse(
                alert.getId(), alert.getKeyword(), alert.getCategoryId(), alert.getJobType(),
                alert.getExperienceLevel(), alert.getWorkMode(), alert.getCity(),
                alert.getFrequency(), alert.isActive()
        );
    }
}

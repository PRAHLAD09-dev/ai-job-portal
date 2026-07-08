package com.prahlad.aijobportal.jobservice.admin.service.impl;

import com.prahlad.aijobportal.jobservice.admin.dto.response.AdminJobResponse;
import com.prahlad.aijobportal.jobservice.admin.dto.response.JobPlatformStatisticsResponse;
import com.prahlad.aijobportal.jobservice.admin.mapper.AdminJobMapper;
import com.prahlad.aijobportal.jobservice.admin.service.AdminJobService;
import com.prahlad.aijobportal.jobservice.admin.specification.AdminJobSpecification;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminJobServiceImpl implements AdminJobService {

    private final JobRepository jobRepository;
    private final AdminJobMapper adminJobMapper;

    @Override
    public Page<AdminJobResponse> searchJobs(String keyword, JobStatus status, UUID companyId, Pageable pageable) {
        return jobRepository.findAll(AdminJobSpecification.withCriteria(keyword, status, companyId), pageable)
                .map(adminJobMapper::toResponse);
    }

    @Override
    public AdminJobResponse getJob(UUID jobId) {
        return adminJobMapper.toResponse(findJobOrThrow(jobId));
    }

    @Override
    @Transactional
    public AdminJobResponse removeJob(UUID jobId) {
        Job job = findJobOrThrow(jobId);
        job.setStatus(JobStatus.ARCHIVED);
        job.setClosedAt(Instant.now());
        log.info("Admin action: job {} removed (archived)", jobId);
        return adminJobMapper.toResponse(job);
    }

    @Override
    @Transactional
    public AdminJobResponse restoreJob(UUID jobId) {
        Job job = findJobOrThrow(jobId);
        job.setStatus(JobStatus.PUBLISHED);
        job.setClosedAt(null);
        if (job.getPublishedAt() == null) {
            job.setPublishedAt(Instant.now());
        }
        log.info("Admin action: job {} restored (published)", jobId);
        return adminJobMapper.toResponse(job);
    }

    @Override
    @Transactional
    public AdminJobResponse featureJob(UUID jobId) {
        Job job = findJobOrThrow(jobId);
        job.setFeatured(true);
        log.info("Admin action: job {} featured", jobId);
        return adminJobMapper.toResponse(job);
    }

    @Override
    @Transactional
    public AdminJobResponse unfeatureJob(UUID jobId) {
        Job job = findJobOrThrow(jobId);
        job.setFeatured(false);
        log.info("Admin action: job {} unfeatured", jobId);
        return adminJobMapper.toResponse(job);
    }

    @Override
    public JobPlatformStatisticsResponse getPlatformStatistics() {
        return new JobPlatformStatisticsResponse(
                jobRepository.count(),
                jobRepository.countByStatus(JobStatus.DRAFT),
                jobRepository.countByStatus(JobStatus.PUBLISHED),
                jobRepository.countByStatus(JobStatus.CLOSED),
                jobRepository.countByStatus(JobStatus.ARCHIVED),
                jobRepository.countByFeaturedTrue()
        );
    }

    private Job findJobOrThrow(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
    }
}

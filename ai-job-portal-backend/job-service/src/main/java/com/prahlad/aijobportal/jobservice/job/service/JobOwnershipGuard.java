package com.prahlad.aijobportal.jobservice.job.service;

import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.exception.JobAccessDeniedException;
import com.prahlad.aijobportal.jobservice.job.exception.JobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Centralizes the "a recruiter may only manage their own company jobs"
 * business rule (PROJECT_SPECIFICATION.md Section 16), so every
 * job-management operation enforces the same ownership check exactly
 * once.
 */
@Service
@RequiredArgsConstructor
public class JobOwnershipGuard {

    private final JobRepository jobRepository;

    /**
     * Resolves a job by id and verifies it belongs to the given
     * company, throwing {@link JobAccessDeniedException} if the job
     * exists but belongs to someone else (rather than a generic
     * not-found, since the recruiter is authenticated and the
     * distinction matters for them), or {@link JobNotFoundException} if
     * the job simply does not exist.
     */
    public Job getOwnedJobOrThrow(UUID jobId, UUID companyId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        if (!job.getCompanyId().equals(companyId)) {
            throw new JobAccessDeniedException("You do not have permission to manage this job");
        }

        return job;
    }
}

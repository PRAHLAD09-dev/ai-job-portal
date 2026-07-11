package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.job.dto.response.SavedJobResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.entity.SavedJob;
import com.prahlad.aijobportal.jobservice.job.exception.JobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.exception.SavedJobAlreadyExistsException;
import com.prahlad.aijobportal.jobservice.job.exception.SavedJobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.mapper.JobMapper;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.jobservice.job.repository.SavedJobRepository;
import com.prahlad.aijobportal.jobservice.job.service.SavedJobService;
import com.prahlad.aijobportal.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public SavedJobResponse saveJob(UUID userId, UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new SavedJobAlreadyExistsException();
        }

        SavedJob savedJob = SavedJob.builder()
                .userId(userId)
                .job(job)
                .build();

        SavedJob saved;
        try {
            // saveAndFlush (not save) so a concurrent saveJob() call for
            // the same (userId, jobId) - which passed the existsBy...
            // check above before either request committed - surfaces its
            // uk_saved_jobs_user_job conflict synchronously, right here,
            // rather than at some later unrelated flush point.
            saved = savedJobRepository.saveAndFlush(savedJob);
        } catch (DataIntegrityViolationException ex) {
            // The race loser: another saveJob() call for this same
            // (userId, jobId) pair committed first.
            throw new SavedJobAlreadyExistsException();
        }
        log.info("User id={} saved job id={}", userId, jobId);
        return new SavedJobResponse(saved.getId(), jobMapper.toSummaryResponse(job), saved.getCreatedAt());
    }

    @Override
    @Transactional
    public void unsaveJob(UUID userId, UUID jobId) {
        SavedJob savedJob = savedJobRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new SavedJobNotFoundException(jobId));
        savedJobRepository.delete(savedJob);
        log.info("User id={} unsaved job id={}", userId, jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SavedJobResponse> getMySavedJobs(UUID userId, Pageable pageable) {
        Page<SavedJob> page = savedJobRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(savedJob ->
                new SavedJobResponse(savedJob.getId(), jobMapper.toSummaryResponse(savedJob.getJob()), savedJob.getCreatedAt())));
    }
}

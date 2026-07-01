package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.job.dto.response.JobSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.SavedJobResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.entity.SavedJob;
import com.prahlad.aijobportal.jobservice.job.exception.JobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.exception.SavedJobAlreadyExistsException;
import com.prahlad.aijobportal.jobservice.job.exception.SavedJobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.mapper.JobMapper;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.jobservice.job.repository.SavedJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavedJobServiceImplTest {

    @Mock private SavedJobRepository savedJobRepository;
    @Mock private JobRepository jobRepository;
    @Mock private JobMapper jobMapper;

    @InjectMocks
    private SavedJobServiceImpl savedJobService;

    private UUID userId;
    private UUID jobId;
    private Job job;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        job = Job.builder().build();
        job.setId(jobId);
    }

    @Test
    void saveJob_savesSuccessfully_whenJobExistsAndNotAlreadySaved() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByUserIdAndJobId(userId, jobId)).thenReturn(false);

        SavedJob persisted = SavedJob.builder().userId(userId).job(job).build();
        persisted.setId(UUID.randomUUID());
        when(savedJobRepository.save(any(SavedJob.class))).thenReturn(persisted);
        when(jobMapper.toSummaryResponse(job)).thenReturn(mockSummary());

        SavedJobResponse response = savedJobService.saveJob(userId, jobId);

        assertThat(response).isNotNull();
        assertThat(response.job()).isNotNull();
        verify(savedJobRepository).save(any(SavedJob.class));
    }

    @Test
    void saveJob_throws_whenJobDoesNotExist() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savedJobService.saveJob(userId, jobId))
                .isInstanceOf(JobNotFoundException.class);

        verify(savedJobRepository, never()).save(any());
    }

    @Test
    void saveJob_throws_whenAlreadySaved() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByUserIdAndJobId(userId, jobId)).thenReturn(true);

        assertThatThrownBy(() -> savedJobService.saveJob(userId, jobId))
                .isInstanceOf(SavedJobAlreadyExistsException.class);

        verify(savedJobRepository, never()).save(any());
    }

    @Test
    void unsaveJob_deletes_whenFound() {
        SavedJob savedJob = SavedJob.builder().userId(userId).job(job).build();
        when(savedJobRepository.findByUserIdAndJobId(userId, jobId)).thenReturn(Optional.of(savedJob));

        savedJobService.unsaveJob(userId, jobId);

        verify(savedJobRepository).delete(savedJob);
    }

    @Test
    void unsaveJob_throws_whenNotFound() {
        when(savedJobRepository.findByUserIdAndJobId(userId, jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savedJobService.unsaveJob(userId, jobId))
                .isInstanceOf(SavedJobNotFoundException.class);
    }

    private JobSummaryResponse mockSummary() {
        return new JobSummaryResponse(jobId, "Acme", null, "Engineering", "Backend Engineer",
                "backend-engineer", null, null, null, null, null, null, null, null,
                false, java.util.List.of(), null);
    }
}

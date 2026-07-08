package com.prahlad.aijobportal.jobservice.admin.service.impl;

import com.prahlad.aijobportal.jobservice.admin.dto.response.AdminJobResponse;
import com.prahlad.aijobportal.jobservice.admin.mapper.AdminJobMapper;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminJobServiceImplTest {

    @Mock private JobRepository jobRepository;
    @Mock private AdminJobMapper adminJobMapper;

    private AdminJobServiceImpl adminJobService;

    private UUID jobId;
    private Job job;

    @BeforeEach
    void setUp() {
        adminJobService = new AdminJobServiceImpl(jobRepository, adminJobMapper);
        jobId = UUID.randomUUID();
        job = Job.builder()
                .title("Senior Engineer")
                .status(JobStatus.PUBLISHED)
                .featured(false)
                .publishedAt(Instant.now())
                .build();
    }

    @Test
    void removeJob_setsStatusToArchivedAndClosedAt() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(adminJobMapper.toResponse(any(Job.class))).thenAnswer(invocation -> {
            Job j = invocation.getArgument(0);
            return new AdminJobResponse(jobId, null, null, j.getTitle(), null, null, null,
                    j.getStatus(), j.isFeatured(), 0, j.getPublishedAt(), j.getClosedAt(), Instant.now());
        });

        AdminJobResponse response = adminJobService.removeJob(jobId);

        assertThat(response.status()).isEqualTo(JobStatus.ARCHIVED);
        assertThat(job.getClosedAt()).isNotNull();
    }

    @Test
    void featureJob_whenJobDoesNotExist_throwsResourceNotFoundException() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminJobService.featureJob(jobId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

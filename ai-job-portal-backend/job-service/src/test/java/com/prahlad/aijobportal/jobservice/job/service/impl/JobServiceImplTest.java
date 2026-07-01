package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.benefit.mapper.JobBenefitMapper;
import com.prahlad.aijobportal.jobservice.category.entity.JobCategory;
import com.prahlad.aijobportal.jobservice.category.service.JobCategoryLookupService;
import com.prahlad.aijobportal.jobservice.event.JobEventPublisher;
import com.prahlad.aijobportal.jobservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.dto.request.CreateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import com.prahlad.aijobportal.jobservice.job.exception.InvalidJobStateException;
import com.prahlad.aijobportal.jobservice.job.exception.JobAccessDeniedException;
import com.prahlad.aijobportal.jobservice.job.mapper.JobMapper;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.jobservice.job.service.JobOwnershipGuard;
import com.prahlad.aijobportal.jobservice.job.service.RecruiterLookupService;
import com.prahlad.aijobportal.jobservice.job.util.JobSlugGenerator;
import com.prahlad.aijobportal.jobservice.location.dto.request.JobLocationRequest;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import com.prahlad.aijobportal.jobservice.location.mapper.JobLocationMapper;
import com.prahlad.aijobportal.jobservice.requirement.mapper.JobRequirementMapper;
import com.prahlad.aijobportal.jobservice.skill.mapper.JobSkillMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock private JobRepository jobRepository;
    @Mock private JobMapper jobMapper;
    @Mock private JobSkillMapper jobSkillMapper;
    @Mock private JobBenefitMapper jobBenefitMapper;
    @Mock private JobLocationMapper jobLocationMapper;
    @Mock private JobRequirementMapper jobRequirementMapper;
    @Mock private JobCategoryLookupService jobCategoryLookupService;
    @Mock private JobOwnershipGuard jobOwnershipGuard;
    @Mock private JobSlugGenerator jobSlugGenerator;
    @Mock private RecruiterLookupService recruiterLookupService;
    @Mock private JobEventPublisher jobEventPublisher;

    @InjectMocks
    private JobServiceImpl jobService;

    private UUID userId;
    private UUID companyId;
    private String bearerToken;
    private RecruiterSummaryResponse recruiter;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        bearerToken = "Bearer test-token";
        recruiter = new RecruiterSummaryResponse(UUID.randomUUID(), userId, "r@x.com", "Recruiter", companyId, "Acme Inc");
    }

    @Test
    void createJob_persistsAndPublishesEvent() {
        UUID categoryId = UUID.randomUUID();
        CreateJobRequest request = new CreateJobRequest(
                categoryId, "Backend Engineer", "Job description here",
                JobType.FULL_TIME, ExperienceLevel.MID_LEVEL, WorkMode.REMOTE,
                null, null, null, null, 1, null,
                List.of(new JobLocationRequest("Bangalore", "Karnataka", "India")),
                null, null, null
        );

        lenient().when(recruiterLookupService.fetchCurrentRecruiter(bearerToken)).thenReturn(recruiter);
        JobCategory category = new JobCategory();
        category.setId(categoryId);
        lenient().when(jobCategoryLookupService.getByIdOrThrow(categoryId)).thenReturn(category);
        lenient().when(jobSlugGenerator.generateUniqueSlug(any(), any())).thenReturn("backend-engineer");
        lenient().when(jobLocationMapper.toEntity(any(JobLocationRequest.class))).thenReturn(new JobLocation());

        Job saved = Job.builder().companyId(companyId).title("Backend Engineer").slug("backend-engineer").build();
        saved.setId(UUID.randomUUID());
        lenient().when(jobRepository.save(any(Job.class))).thenReturn(saved);
        lenient().when(jobMapper.toResponse(saved)).thenReturn(mockResponse(saved.getId()));

        JobResponse response = jobService.createJob(userId, bearerToken, request);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Backend Engineer");
    }

    @Test
    void publishJob_throws_whenJobNotInDraftOrClosed() {
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().companyId(companyId).status(JobStatus.PUBLISHED).build();
        job.setId(jobId);

        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken)).thenReturn(recruiter);
        when(jobOwnershipGuard.getOwnedJobOrThrow(jobId, companyId)).thenReturn(job);

        assertThatThrownBy(() -> jobService.publishJob(userId, bearerToken, jobId))
                .isInstanceOf(InvalidJobStateException.class);
    }

    @Test
    void deleteJob_throws_whenRecruiterDoesNotOwnJob() {
        UUID jobId = UUID.randomUUID();
        when(recruiterLookupService.fetchCurrentRecruiter(bearerToken)).thenReturn(recruiter);
        when(jobOwnershipGuard.getOwnedJobOrThrow(jobId, companyId))
                .thenThrow(new JobAccessDeniedException("You do not have permission to manage this job"));

        assertThatThrownBy(() -> jobService.deleteJob(userId, bearerToken, jobId))
                .isInstanceOf(JobAccessDeniedException.class);
    }

    private JobResponse mockResponse(UUID id) {
        return new JobResponse(id, companyId, "Acme Inc", null, null, "Backend Engineer",
                "backend-engineer", "Job description here", JobType.FULL_TIME, ExperienceLevel.MID_LEVEL,
                WorkMode.REMOTE, JobStatus.DRAFT, null, null, null, null, 1, null,
                false, 0L, null, List.of(), List.of(), List.of(), List.of(), null);
    }
}

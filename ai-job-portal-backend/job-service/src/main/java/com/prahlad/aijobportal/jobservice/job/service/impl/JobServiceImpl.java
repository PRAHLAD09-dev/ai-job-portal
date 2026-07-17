package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.benefit.entity.JobBenefit;
import com.prahlad.aijobportal.jobservice.benefit.mapper.JobBenefitMapper;
import com.prahlad.aijobportal.jobservice.category.entity.JobCategory;
import com.prahlad.aijobportal.jobservice.category.service.JobCategoryLookupService;
import com.prahlad.aijobportal.jobservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.jobservice.event.dto.JobClosedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobCreatedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobDeletedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobPublishedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobUpdatedEvent;
import com.prahlad.aijobportal.jobservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.dto.request.CreateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.request.JobSearchCriteria;
import com.prahlad.aijobportal.jobservice.job.dto.request.UpdateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSavedCountResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobStatisticsResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.ApplyMethod;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.exception.InvalidJobStateException;
import com.prahlad.aijobportal.jobservice.job.exception.JobNotFoundException;
import com.prahlad.aijobportal.jobservice.job.mapper.JobMapper;
import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import com.prahlad.aijobportal.jobservice.job.repository.SavedJobRepository;
import com.prahlad.aijobportal.jobservice.job.service.JobOwnershipGuard;
import com.prahlad.aijobportal.jobservice.job.service.JobService;
import com.prahlad.aijobportal.jobservice.job.service.RecruiterLookupService;
import com.prahlad.aijobportal.jobservice.job.specification.JobSpecification;
import com.prahlad.aijobportal.jobservice.job.util.JobSlugGenerator;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import com.prahlad.aijobportal.jobservice.location.mapper.JobLocationMapper;
import com.prahlad.aijobportal.jobservice.requirement.entity.JobRequirement;
import com.prahlad.aijobportal.jobservice.requirement.mapper.JobRequirementMapper;
import com.prahlad.aijobportal.jobservice.skill.entity.JobSkill;
import com.prahlad.aijobportal.jobservice.skill.mapper.JobSkillMapper;
import com.prahlad.aijobportal.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private static final int FEATURED_LIMIT = 10;
    private static final int LATEST_LIMIT = 10;
    private static final int TRENDING_LIMIT = 10;
    private static final int SIMILAR_LIMIT = 6;

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobMapper jobMapper;
    private final JobSkillMapper jobSkillMapper;
    private final JobBenefitMapper jobBenefitMapper;
    private final JobLocationMapper jobLocationMapper;
    private final JobRequirementMapper jobRequirementMapper;
    private final JobCategoryLookupService jobCategoryLookupService;
    private final JobOwnershipGuard jobOwnershipGuard;
    private final JobSlugGenerator jobSlugGenerator;
    private final RecruiterLookupService recruiterLookupService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE,
            RedisCacheConfig.POPULAR_SKILLS_CACHE }, allEntries = true)
    public JobResponse createJob(UUID userId, String bearerToken, CreateJobRequest request) {
        RecruiterSummaryResponse recruiter = recruiterLookupService.fetchCurrentRecruiter(bearerToken);
        JobCategory category = jobCategoryLookupService.getByIdOrThrow(request.categoryId());

        String slug = jobSlugGenerator.generateUniqueSlug(request.title(), recruiter.companyName());

        Job job = Job.builder()
                .companyId(recruiter.companyId())
                .recruiterId(recruiter.id())
                .companyName(recruiter.companyName())
                .category(category)
                .title(request.title())
                .slug(slug)
                .description(request.description())
                .jobType(request.jobType())
                .experienceLevel(request.experienceLevel())
                .workMode(request.workMode())
                .status(JobStatus.DRAFT)
                .minSalary(request.minSalary())
                .maxSalary(request.maxSalary())
                .salaryType(request.salaryType())
                .currency(request.currency())
                .vacancies(request.vacancies() > 0 ? request.vacancies() : 1)
                .applicationDeadline(request.applicationDeadline())
                .applyMethod(request.applyMethod())
                .externalApplyUrl(request.applyMethod() == ApplyMethod.EXTERNAL_APPLY
                        ? request.externalApplyUrl()
                        : null)
                .build();

        attachChildren(job, request.locations(), request.skills(), request.benefits(), request.requirements());

        Job saved = jobRepository.save(job);

        applicationEventPublisher.publishEvent(new JobCreatedEvent(
                saved.getId(), saved.getCompanyId(), userId, saved.getTitle(), Instant.now()));

        log.info("Created job id={} for companyId={}", saved.getId(), saved.getCompanyId());
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE, RedisCacheConfig.FEATURED_JOBS_CACHE,
            RedisCacheConfig.TRENDING_JOBS_CACHE, RedisCacheConfig.POPULAR_SKILLS_CACHE }, allEntries = true)
    public JobResponse updateJob(UUID userId, String bearerToken, UUID jobId, UpdateJobRequest request) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        JobCategory category = jobCategoryLookupService.getByIdOrThrow(request.categoryId());

        job.setCategory(category);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setJobType(request.jobType());
        job.setExperienceLevel(request.experienceLevel());
        job.setWorkMode(request.workMode());
        job.setMinSalary(request.minSalary());
        job.setMaxSalary(request.maxSalary());
        job.setSalaryType(request.salaryType());
        job.setCurrency(request.currency());
        job.setVacancies(request.vacancies() > 0 ? request.vacancies() : 1);
        job.setApplicationDeadline(request.applicationDeadline());
        job.setApplyMethod(request.applyMethod());
        job.setExternalApplyUrl(request.applyMethod() == ApplyMethod.EXTERNAL_APPLY
                ? request.externalApplyUrl()
                : null);

        job.getLocations().clear();
        job.getSkills().clear();
        job.getBenefits().clear();
        job.getRequirements().clear();
        attachChildren(job, request.locations(), request.skills(), request.benefits(), request.requirements());

        Job saved = jobRepository.save(job);

        applicationEventPublisher.publishEvent(new JobUpdatedEvent(
                saved.getId(), saved.getCompanyId(), saved.getTitle(), Instant.now()));

        log.info("Updated job id={}", saved.getId());
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE, RedisCacheConfig.FEATURED_JOBS_CACHE,
            RedisCacheConfig.TRENDING_JOBS_CACHE }, allEntries = true)
    public void deleteJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        jobRepository.delete(job);

        applicationEventPublisher.publishEvent(new JobDeletedEvent(jobId, job.getCompanyId(), Instant.now()));
        log.info("Deleted job id={}", jobId);
    }

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE, RedisCacheConfig.FEATURED_JOBS_CACHE,
            RedisCacheConfig.TRENDING_JOBS_CACHE }, allEntries = true)
    public JobResponse publishJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        if (job.getStatus() != JobStatus.DRAFT && job.getStatus() != JobStatus.CLOSED) {
            throw new InvalidJobStateException("Only draft or closed jobs can be published");
        }

        job.setStatus(JobStatus.PUBLISHED);
        job.setPublishedAt(Instant.now());
        job.setClosedAt(null);
        Job saved = jobRepository.save(job);

        applicationEventPublisher.publishEvent(new JobPublishedEvent(
                saved.getId(), saved.getCompanyId(), saved.getTitle(), Instant.now()));

        log.info("Published job id={}", saved.getId());
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE, RedisCacheConfig.FEATURED_JOBS_CACHE,
            RedisCacheConfig.TRENDING_JOBS_CACHE }, allEntries = true)
    public JobResponse closeJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new InvalidJobStateException("Only published jobs can be closed");
        }

        job.setStatus(JobStatus.CLOSED);
        job.setClosedAt(Instant.now());
        Job saved = jobRepository.save(job);

        applicationEventPublisher.publishEvent(new JobClosedEvent(
                saved.getId(), saved.getCompanyId(), saved.getTitle(), Instant.now()));

        log.info("Closed job id={}", saved.getId());
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = { RedisCacheConfig.LATEST_JOBS_CACHE, RedisCacheConfig.FEATURED_JOBS_CACHE,
            RedisCacheConfig.TRENDING_JOBS_CACHE }, allEntries = true)
    public JobResponse reopenJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        if (job.getStatus() != JobStatus.CLOSED && job.getStatus() != JobStatus.ARCHIVED) {
            throw new InvalidJobStateException("Only closed or archived jobs can be reopened");
        }

        job.setStatus(JobStatus.PUBLISHED);
        job.setPublishedAt(Instant.now());
        job.setClosedAt(null);
        Job saved = jobRepository.save(job);

        log.info("Reopened job id={}", saved.getId());
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public JobResponse duplicateJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job original = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());

        String slug = jobSlugGenerator.generateUniqueSlug(original.getTitle() + "-copy", recruiter.companyName());

        Job duplicate = Job.builder()
                .companyId(original.getCompanyId())
                .recruiterId(original.getRecruiterId())
                .companyName(original.getCompanyName())
                .companyLogoUrl(original.getCompanyLogoUrl())
                .category(original.getCategory())
                .title(original.getTitle() + " (Copy)")
                .slug(slug)
                .description(original.getDescription())
                .jobType(original.getJobType())
                .experienceLevel(original.getExperienceLevel())
                .workMode(original.getWorkMode())
                .status(JobStatus.DRAFT)
                .minSalary(original.getMinSalary())
                .maxSalary(original.getMaxSalary())
                .salaryType(original.getSalaryType())
                .currency(original.getCurrency())
                .vacancies(original.getVacancies())
                .applicationDeadline(null)
                .build();

        for (JobLocation location : original.getLocations()) {
            JobLocation copy = JobLocation.builder()
                    .city(location.getCity()).state(location.getState()).country(location.getCountry())
                    .build();
            copy.setJob(duplicate);
            duplicate.getLocations().add(copy);
        }
        for (JobSkill skill : original.getSkills()) {
            JobSkill copy = JobSkill.builder()
                    .name(skill.getName()).requiredProficiency(skill.getRequiredProficiency())
                    .mandatory(skill.isMandatory()).build();
            copy.setJob(duplicate);
            duplicate.getSkills().add(copy);
        }
        for (JobBenefit benefit : original.getBenefits()) {
            JobBenefit copy = JobBenefit.builder().title(benefit.getTitle()).description(benefit.getDescription())
                    .build();
            copy.setJob(duplicate);
            duplicate.getBenefits().add(copy);
        }
        for (JobRequirement requirement : original.getRequirements()) {
            JobRequirement copy = JobRequirement.builder()
                    .type(requirement.getType()).description(requirement.getDescription())
                    .displayOrder(requirement.getDisplayOrder()).build();
            copy.setJob(duplicate);
            duplicate.getRequirements().add(copy);
        }

        Job saved = jobRepository.save(duplicate);
        log.info("Duplicated job id={} from id={}", saved.getId(), jobId);
        return jobMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse previewJob(UUID userId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Job job = jobOwnershipGuard.getOwnedJobOrThrow(jobId, recruiter.companyId());
        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobSummaryResponse> getMyCompanyJobs(UUID userId, String bearerToken, Pageable pageable) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        Page<Job> page = jobRepository.findByCompanyId(recruiter.companyId(), pageable);
        return PageResponse.from(page.map(jobMapper::toSummaryResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public JobStatisticsResponse getMyCompanyStatistics(UUID userId, String bearerToken) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        UUID companyId = recruiter.companyId();

        long total = jobRepository.countByCompanyId(companyId);
        long active = jobRepository.countByCompanyIdAndStatus(companyId, JobStatus.PUBLISHED);
        long closed = jobRepository.countByCompanyIdAndStatus(companyId, JobStatus.CLOSED);
        long draft = jobRepository.countByCompanyIdAndStatus(companyId, JobStatus.DRAFT);

        return new JobStatisticsResponse(companyId, total, active, closed, draft);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSavedCountResponse> getMyCompanySavedJobStatistics(UUID userId, String bearerToken) {
        RecruiterSummaryResponse recruiter = resolveCurrentRecruiter(bearerToken);
        return savedJobRepository.countSavedByCompanyGroupedByJob(recruiter.companyId()).stream()
                .map(row -> new JobSavedCountResponse(row.getJobId(), row.getJobTitle(), row.getSavedCount()))
                .toList();
    }

    @Override
    @Transactional
    public JobResponse getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
        // Mirror the pending increment in-memory and map to the response DTO
        // FIRST, while 'job' is still attached to the Hibernate session - the
        // mapper (via MapStruct) touches lazy associations (category, skills,
        // benefits, locations, requirements), which requires an active
        // session to initialize.
        // incrementViewCount is a @Modifying(clearAutomatically = true) bulk
        // update: it atomically increments view_count in the DB. Running it
        // LAST means the persistence-context clear it triggers can no longer
        // detach 'job' before the lazy associations have been read above.
        job.setViewCount(job.getViewCount() + 1);
        JobResponse response = jobMapper.toResponse(job);
        jobRepository.incrementViewCount(jobId);
        return response;
    }

    @Override
    @Transactional
    public JobResponse getJobBySlug(String slug) {
        Job job = jobRepository.findBySlug(slug)
                .orElseThrow(() -> new JobNotFoundException(slug));
        // Same ordering fix as getJobById: map while still attached, then
        // run the clearing bulk-update last.
        job.setViewCount(job.getViewCount() + 1);
        JobResponse response = jobMapper.toResponse(job);
        jobRepository.incrementViewCount(job.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobSummaryResponse> searchJobs(JobSearchCriteria criteria, Pageable pageable) {
        Page<Job> page = jobRepository.findAll(JobSpecification.withCriteria(criteria, true), pageable);
        return PageResponse.from(page.map(jobMapper::toSummaryResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getLatestJobs() {
        Pageable pageable = PageRequest.of(0, LATEST_LIMIT, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return jobRepository.findByStatusOrderByPublishedAtDesc(JobStatus.PUBLISHED, pageable)
                .map(jobMapper::toSummaryResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getFeaturedJobs() {
        Pageable pageable = PageRequest.of(0, FEATURED_LIMIT, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return jobRepository.findByStatusAndFeaturedTrueOrderByPublishedAtDesc(JobStatus.PUBLISHED, pageable)
                .map(jobMapper::toSummaryResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getTrendingJobs() {
        Pageable pageable = PageRequest.of(0, TRENDING_LIMIT, Sort.by(Sort.Direction.DESC, "viewCount"));
        return jobRepository.findAll(JobSpecification.withCriteria(null, true), pageable)
                .map(jobMapper::toSummaryResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> getSimilarJobs(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        JobSearchCriteria criteria = new JobSearchCriteria(
                null, job.getCategory().getId(), null, null, null, null,
                null, job.getJobType(), null, null, null, null, null);

        Pageable pageable = PageRequest.of(0, SIMILAR_LIMIT, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return jobRepository.findAll(JobSpecification.withCriteria(criteria, true), pageable)
                .getContent().stream()
                .filter(candidate -> !candidate.getId().equals(jobId))
                .map(jobMapper::toSummaryResponse)
                .toList();
    }

    // ---- internal helpers ----

    private void attachChildren(Job job,
            List<com.prahlad.aijobportal.jobservice.location.dto.request.JobLocationRequest> locations,
            List<com.prahlad.aijobportal.jobservice.skill.dto.request.JobSkillRequest> skills,
            List<com.prahlad.aijobportal.jobservice.benefit.dto.request.JobBenefitRequest> benefits,
            List<com.prahlad.aijobportal.jobservice.requirement.dto.request.JobRequirementRequest> requirements) {
        if (locations != null) {
            locations.forEach(locationRequest -> {
                JobLocation location = jobLocationMapper.toEntity(locationRequest);
                location.setJob(job);
                job.getLocations().add(location);
            });
        }
        if (skills != null) {
            skills.forEach(skillRequest -> {
                JobSkill skill = jobSkillMapper.toEntity(skillRequest);
                skill.setJob(job);
                job.getSkills().add(skill);
            });
        }
        if (benefits != null) {
            benefits.forEach(benefitRequest -> {
                JobBenefit benefit = jobBenefitMapper.toEntity(benefitRequest);
                benefit.setJob(job);
                job.getBenefits().add(benefit);
            });
        }
        if (requirements != null) {
            requirements.forEach(requirementRequest -> {
                JobRequirement requirement = jobRequirementMapper.toEntity(requirementRequest);
                requirement.setJob(job);
                job.getRequirements().add(requirement);
            });
        }
    }

    private RecruiterSummaryResponse resolveCurrentRecruiter(String bearerToken) {
        return recruiterLookupService.fetchCurrentRecruiter(bearerToken);
    }
}

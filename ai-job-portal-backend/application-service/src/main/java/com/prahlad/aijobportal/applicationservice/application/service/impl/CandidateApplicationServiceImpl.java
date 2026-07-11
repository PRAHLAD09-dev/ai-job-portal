package com.prahlad.aijobportal.applicationservice.application.service.impl;

import com.prahlad.aijobportal.applicationservice.application.dto.request.CreateApplicationRequest;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationResponse;
import com.prahlad.aijobportal.applicationservice.application.dto.response.ApplicationSummaryResponse;
import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.application.exception.ApplicationNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.exception.DuplicateApplicationException;
import com.prahlad.aijobportal.applicationservice.application.exception.JobClosedException;
import com.prahlad.aijobportal.applicationservice.application.exception.ResumeNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.mapper.ApplicationMapper;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import com.prahlad.aijobportal.applicationservice.application.service.ApplicationService;
import com.prahlad.aijobportal.applicationservice.application.service.CandidateApplicationService;
import com.prahlad.aijobportal.applicationservice.application.service.CandidateLookupService;
import com.prahlad.aijobportal.applicationservice.application.service.JobLookupService;
import com.prahlad.aijobportal.applicationservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationCreatedEvent;
import com.prahlad.aijobportal.applicationservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.applicationservice.feign.dto.JobSummaryResponse;
import com.prahlad.aijobportal.applicationservice.feign.dto.ResumeStatus;
import com.prahlad.aijobportal.applicationservice.timeline.service.ApplicationTimelineService;
import com.prahlad.aijobportal.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateApplicationServiceImpl implements CandidateApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationService applicationService;
    private final ApplicationTimelineService applicationTimelineService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JobLookupService jobLookupService;
    private final CandidateLookupService candidateLookupService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CANDIDATE_DASHBOARD_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.RECENT_APPLICATIONS_CACHE, allEntries = true)
    })
    public ApplicationResponse apply(UUID candidateUserId, String bearerToken, CreateApplicationRequest request) {
        JobSummaryResponse job = jobLookupService.fetchJob(request.jobId());
        validateJobIsOpen(job);

        CandidateProfileSummaryResponse candidate = candidateLookupService.fetchCurrentCandidate(bearerToken);
        String resumeUrl = resolveResumeUrl(candidate, request.resumeId());

        if (applicationRepository.existsByJobIdAndCandidateId(request.jobId(), candidate.id())) {
            throw new DuplicateApplicationException();
        }

        Instant now = Instant.now();
        JobApplication application = JobApplication.builder()
                .candidateId(candidate.id())
                .candidateUserId(candidateUserId)
                .candidateName(candidate.fullName())
                .candidateEmail(candidate.email())
                .companyId(job.companyId())
                .companyName(job.companyName())
                .jobId(job.id())
                .jobTitle(job.title())
                .resumeUrl(resumeUrl)
                .coverLetter(request.coverLetter())
                .status(ApplicationStatus.APPLIED)
                .appliedAt(now)
                .build();

        JobApplication saved;
        try {
            // saveAndFlush (not save) so a concurrent apply() call for the
            // same (jobId, candidateId) - which passed the existsBy...
            // check above before either request committed - surfaces its
            // uk_application_job_candidate conflict synchronously, right
            // here, rather than at some later unrelated flush point.
            saved = applicationRepository.saveAndFlush(application);
        } catch (DataIntegrityViolationException ex) {
            // The race loser: another apply() call for this same
            // (jobId, candidateId) pair committed first.
            throw new DuplicateApplicationException();
        }

        applicationTimelineService.recordTransition(saved, null, ApplicationStatus.APPLIED, candidateUserId,
                "Application submitted");

        applicationEventPublisher.publishEvent(new ApplicationCreatedEvent(
                saved.getId(), saved.getJobId(), saved.getCandidateId(), saved.getCandidateUserId(),
                saved.getCompanyId(), saved.getJobTitle(), saved.getAppliedAt()));

        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CANDIDATE_DASHBOARD_CACHE, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.RECENT_APPLICATIONS_CACHE, allEntries = true)
    })
    public void withdraw(UUID candidateUserId, UUID applicationId) {
        JobApplication application = getOwnedByCandidateOrThrow(candidateUserId, applicationId);
        applicationService.transitionStatus(application, ApplicationStatus.WITHDRAWN, candidateUserId,
                "Withdrawn by candidate", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationDetail(UUID candidateUserId, UUID applicationId) {
        return applicationMapper.toResponse(getOwnedByCandidateOrThrow(candidateUserId, applicationId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationSummaryResponse> getMyApplications(UUID candidateUserId, Pageable pageable) {
        Page<ApplicationSummaryResponse> page = applicationRepository.findByCandidateUserId(candidateUserId, pageable)
                .map(applicationMapper::toSummaryResponse);
        return PageResponse.from(page);
    }

    private JobApplication getOwnedByCandidateOrThrow(UUID candidateUserId, UUID applicationId) {
        return applicationRepository.findByIdAndCandidateUserId(applicationId, candidateUserId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
    }

    private void validateJobIsOpen(JobSummaryResponse job) {
        if (job == null || !"PUBLISHED".equals(job.status())) {
            throw new JobClosedException();
        }
        if (job.applicationDeadline() != null && job.applicationDeadline().isBefore(Instant.now())) {
            throw new JobClosedException();
        }
    }

    private String resolveResumeUrl(CandidateProfileSummaryResponse candidate, UUID resumeId) {
        List<CandidateProfileSummaryResponse.ResumeSummaryResponse> resumes = candidate.resumes();
        if (resumes == null || resumes.isEmpty()) {
            throw new ResumeNotFoundException();
        }
        if (resumeId != null) {
            return resumes.stream()
                    .filter(resume -> resume.id().equals(resumeId))
                    .findFirst()
                    .orElseThrow(ResumeNotFoundException::new)
                    .fileUrl();
        }
        // No resumeId supplied: use the candidate's current resume. This
        // list may contain ARCHIVED resumes alongside the ACTIVE one (kept
        // for version history), in no guaranteed order - Candidate Service
        // has at most one ACTIVE resume per candidate at any time, so
        // filtering on status is the only deterministic way to find it.
        // Previously this picked resumes.get(resumes.size() - 1), which
        // assumed list position meant recency; that assumption doesn't
        // hold for a @OneToMany collection with no @OrderBy, and could
        // silently attach an outdated archived resume to the application.
        return resumes.stream()
                .filter(resume -> resume.status() == ResumeStatus.ACTIVE)
                .findFirst()
                .orElseThrow(ResumeNotFoundException::new)
                .fileUrl();
    }
}

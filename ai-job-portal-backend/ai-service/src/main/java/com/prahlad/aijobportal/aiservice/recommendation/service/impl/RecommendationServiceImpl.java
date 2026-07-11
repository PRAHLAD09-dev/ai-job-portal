package com.prahlad.aijobportal.aiservice.recommendation.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.event.AiEventPublisher;
import com.prahlad.aijobportal.aiservice.event.dto.CandidateRankedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.RecommendationGeneratedEvent;
import com.prahlad.aijobportal.aiservice.exception.AiAccessDeniedException;
import com.prahlad.aijobportal.aiservice.external.ApplicationLookupService;
import com.prahlad.aijobportal.aiservice.external.CandidateLookupService;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.external.RecruiterLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.ApplicationSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobLiteResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.gemini.UntrustedTextGuard;
import com.prahlad.aijobportal.aiservice.recommendation.dto.RecommendationAiResult;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.CandidateRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.JobRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.entity.JobRecommendation;
import com.prahlad.aijobportal.aiservice.recommendation.repository.JobRecommendationRepository;
import com.prahlad.aijobportal.aiservice.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private static final String JOB_RECOMMENDATION_PROMPT = """
            You are a job matching assistant. Given a candidate's profile and a pool
            of open jobs, rank the jobs that best fit the candidate. Return a JSON
            object with a single field "recommendations": an array of objects, each
            with "id" (the exact jobId string as given below, unmodified),
            "matchScore" (integer 0-100), and "reasoning" (a short string explaining
            the match). Include only jobs that are a reasonable fit; omit poor
            matches entirely. Order the array from best match to worst.

            %s

            Candidate profile:
            %s

            Open jobs (id | title | company | type | experience level | work mode):
            %s
            """;

    private static final String CANDIDATE_RECOMMENDATION_PROMPT = """
            You are a recruiting assistant helping rank applicants for a job. Given
            the job description and a pool of applicants, rank the applicants by
            fit. Return a JSON object with a single field "recommendations": an
            array of objects, each with "id" (the exact applicationId string as
            given below, unmodified), "matchScore" (integer 0-100), and "reasoning"
            (a short string explaining the fit). Order the array from best match to
            worst.

            %s

            Job:
            %s

            Applicants (applicationId | candidateName):
            %s
            """;

    private final JobRecommendationRepository jobRecommendationRepository;
    private final CandidateLookupService candidateLookupService;
    private final JobLookupService jobLookupService;
    private final RecruiterLookupService recruiterLookupService;
    private final ApplicationLookupService applicationLookupService;
    private final AiStructuredResponseService aiStructuredResponseService;
    private final AiEventPublisher aiEventPublisher;

    @Override
    @Transactional
    @Cacheable(cacheNames = RedisCacheConfig.JOB_RECOMMENDATIONS_CACHE, key = "#candidateId")
    public List<JobRecommendationResponse> recommendJobs(UUID candidateId, String bearerToken) {
        CandidateProfileSummaryResponse candidate = candidateLookupService.fetchCurrentCandidate(bearerToken);
        List<JobLiteResponse> jobs = jobLookupService.fetchLatestJobs();

        if (jobs.isEmpty()) {
            return List.of();
        }

        Map<String, JobLiteResponse> jobsById = jobs.stream()
                .collect(Collectors.toMap(job -> job.id().toString(), job -> job, (a, b) -> a));

        String jobsBlock = jobs.stream()
                .map(job -> "%s | %s | %s | %s | %s | %s".formatted(
                        job.id(), job.title(), job.companyName(), job.jobType(), job.experienceLevel(), job.workMode()))
                .collect(Collectors.joining("\n"));

        RecommendationAiResult aiResult = aiStructuredResponseService.generateStructured(
                JOB_RECOMMENDATION_PROMPT.formatted(UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("CANDIDATE_PROFILE", describeCandidate(candidate)),
                        UntrustedTextGuard.wrap("OPEN_JOBS", jobsBlock)),
                RecommendationAiResult.class);

        jobRecommendationRepository.deleteByCandidateId(candidateId);

        List<JobRecommendationResponse> responses = aiResult.recommendations().stream()
                .filter(item -> jobsById.containsKey(item.id()))
                .map(item -> {
                    JobLiteResponse job = jobsById.get(item.id());
                    int clampedScore = Math.max(0, Math.min(100, item.matchScore()));

                    jobRecommendationRepository.save(JobRecommendation.builder()
                            .candidateId(candidateId)
                            .jobId(job.id())
                            .matchScore(clampedScore)
                            .reasoning(item.reasoning())
                            .build());

                    return new JobRecommendationResponse(job.id(), job.title(), job.companyName(), clampedScore, item.reasoning());
                })
                .sorted(Comparator.comparingInt(JobRecommendationResponse::matchScore).reversed())
                .toList();

        log.info("Generated {} job recommendations for candidateId={}", responses.size(), candidateId);

        aiEventPublisher.publishRecommendationGenerated(new RecommendationGeneratedEvent(
                candidateId, candidateId, responses.size(), Instant.now()));

        return responses;
    }

    @Override
    @Cacheable(cacheNames = RedisCacheConfig.CANDIDATE_RECOMMENDATIONS_CACHE, key = "#jobId")
    public List<CandidateRecommendationResponse> recommendCandidates(UUID recruiterUserId, String bearerToken, UUID jobId) {
        RecruiterSummaryResponse recruiter = recruiterLookupService.fetchCurrentRecruiter(bearerToken);
        JobDetailSummaryResponse job = jobLookupService.fetchJob(jobId);

        if (!job.companyId().equals(recruiter.companyId())) {
            throw new AiAccessDeniedException("You may only rank candidates for jobs belonging to your own company");
        }

        List<ApplicationSummaryResponse> applications = applicationLookupService.fetchApplicationsForJob(bearerToken, jobId);
        if (applications.isEmpty()) {
            return List.of();
        }

        Map<String, ApplicationSummaryResponse> applicationsById = applications.stream()
                .collect(Collectors.toMap(app -> app.id().toString(), app -> app, (a, b) -> a));

        String applicantsBlock = applications.stream()
                .map(app -> "%s | %s".formatted(app.id(), app.candidateName()))
                .collect(Collectors.joining("\n"));

        RecommendationAiResult aiResult = aiStructuredResponseService.generateStructured(
                CANDIDATE_RECOMMENDATION_PROMPT.formatted(UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("JOB", describeJob(job)),
                        UntrustedTextGuard.wrap("APPLICANTS", applicantsBlock)),
                RecommendationAiResult.class);

        List<CandidateRecommendationResponse> responses = aiResult.recommendations().stream()
                .filter(item -> applicationsById.containsKey(item.id()))
                .map(item -> {
                    ApplicationSummaryResponse app = applicationsById.get(item.id());
                    int clampedScore = Math.max(0, Math.min(100, item.matchScore()));
                    return new CandidateRecommendationResponse(app.id(), app.candidateId(), app.candidateName(), clampedScore, item.reasoning());
                })
                .sorted(Comparator.comparingInt(CandidateRecommendationResponse::matchScore).reversed())
                .toList();

        log.info("Ranked {} candidates for jobId={}", responses.size(), jobId);

        aiEventPublisher.publishCandidateRanked(new CandidateRankedEvent(
                jobId, recruiter.companyId(),
                responses.stream().map(CandidateRecommendationResponse::candidateId).toList(),
                Instant.now()));

        return responses;
    }

    private String describeCandidate(CandidateProfileSummaryResponse candidate) {
        String skills = candidate.skills() == null ? "" : candidate.skills().stream()
                .map(CandidateProfileSummaryResponse.SkillSummaryResponse::name)
                .collect(Collectors.joining(", "));
        return "Name: %s\nHeadline: %s\nSummary: %s\nLocation: %s, %s, %s\nSkills: %s".formatted(
                candidate.fullName(), candidate.headline(), candidate.summary(),
                candidate.city(), candidate.state(), candidate.country(), skills);
    }

    private String describeJob(JobDetailSummaryResponse job) {
        return "Title: %s\nCompany: %s\nType: %s\nExperience Level: %s\nDescription: %s".formatted(
                job.title(), job.companyName(), job.jobType(), job.experienceLevel(), job.description());
    }
}

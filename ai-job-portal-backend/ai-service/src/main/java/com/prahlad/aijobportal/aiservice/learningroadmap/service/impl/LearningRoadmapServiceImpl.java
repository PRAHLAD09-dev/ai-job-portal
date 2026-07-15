package com.prahlad.aijobportal.aiservice.learningroadmap.service.impl;

import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.external.CandidateLookupService;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobLiteResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.gemini.UntrustedTextGuard;
import com.prahlad.aijobportal.aiservice.learningroadmap.dto.response.LearningRoadmapResponse;
import com.prahlad.aijobportal.aiservice.learningroadmap.service.LearningRoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningRoadmapServiceImpl implements LearningRoadmapService {

    private static final String PROMPT_TEMPLATE = """
            You are a career development advisor. Given a candidate's current
            skills, headline, and a sample of currently open job titles/types
            on the platform, design a learning roadmap that closes the gap
            between where the candidate is now and what the job market wants.
            Return a JSON object with these exact fields:
            - beginnerTopics: an array of short strings naming foundational topics the candidate should start with
            - intermediateTopics: an array of short strings naming topics to tackle once the beginner topics are solid
            - advancedTopics: an array of short strings naming advanced topics that make the candidate stand out
            - suggestedResources: an array of short strings naming the kind of resource to use for each stage (course, book, official docs, project-based practice, etc.) - do not invent specific URLs
            - practiceOrder: an array of short strings giving a recommended, ordered sequence of hands-on practice milestones across the roadmap

            %s

            Candidate's current skills:
            %s

            Candidate's headline:
            %s

            Sample of open jobs (title | type | experience level):
            %s
            """;

    private final CandidateLookupService candidateLookupService;
    private final JobLookupService jobLookupService;
    private final AiStructuredResponseService aiStructuredResponseService;

    @Override
    @Cacheable(cacheNames = RedisCacheConfig.LEARNING_ROADMAP_CACHE, key = "#candidateId")
    public LearningRoadmapResponse generate(UUID candidateId, String bearerToken) {
        CandidateProfileSummaryResponse candidate = candidateLookupService.fetchCurrentCandidate(bearerToken);
        List<JobLiteResponse> jobs = jobLookupService.fetchLatestJobs();

        List<String> currentSkills = candidate.skills() == null ? List.of() : candidate.skills().stream()
                .map(CandidateProfileSummaryResponse.SkillSummaryResponse::name)
                .toList();

        String jobsBlock = jobs.stream()
                .map(job -> "%s | %s | %s".formatted(job.title(), job.jobType(), job.experienceLevel()))
                .collect(Collectors.joining("\n"));

        LearningRoadmapResponse aiResult = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(
                        UntrustedTextGuard.INSTRUCTION,
                        UntrustedTextGuard.wrap("CANDIDATE_SKILLS", String.join(", ", currentSkills)),
                        UntrustedTextGuard.wrap("CANDIDATE_HEADLINE", candidate.headline()),
                        UntrustedTextGuard.wrap("OPEN_JOBS_SAMPLE", jobsBlock)),
                LearningRoadmapResponse.class);

        log.info("Generated learning roadmap for candidateId={}", candidateId);

        return aiResult;
    }
}

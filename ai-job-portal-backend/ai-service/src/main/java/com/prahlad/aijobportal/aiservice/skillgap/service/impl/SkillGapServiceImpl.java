package com.prahlad.aijobportal.aiservice.skillgap.service.impl;

import com.prahlad.aijobportal.aiservice.external.CandidateLookupService;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobLiteResponse;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.skillgap.dto.response.SkillGapResponse;
import com.prahlad.aijobportal.aiservice.skillgap.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillGapServiceImpl implements SkillGapService {

    private static final String PROMPT_TEMPLATE = """
            You are a career development advisor. Given a candidate's current
            skills and a sample of currently open job titles/types on the
            platform, identify the skill gap. Return a JSON object with these
            exact fields:
            - missingSkills: an array of short strings naming skills the candidate is likely missing for roles they'd want, based on the job market sample
            - careerSuggestions: an array of short, actionable strings suggesting career moves or learning paths

            Candidate's current skills: %s
            Candidate's headline: %s

            Sample of open jobs (title | type | experience level):
            %s
            """;

    private final CandidateLookupService candidateLookupService;
    private final JobLookupService jobLookupService;
    private final AiStructuredResponseService aiStructuredResponseService;

    @Override
    public SkillGapResponse analyze(UUID candidateId, String bearerToken) {
        CandidateProfileSummaryResponse candidate = candidateLookupService.fetchCurrentCandidate(bearerToken);
        List<JobLiteResponse> jobs = jobLookupService.fetchLatestJobs();

        List<String> currentSkills = candidate.skills() == null ? List.of() : candidate.skills().stream()
                .map(CandidateProfileSummaryResponse.SkillSummaryResponse::name)
                .toList();

        String jobsBlock = jobs.stream()
                .map(job -> "%s | %s | %s".formatted(job.title(), job.jobType(), job.experienceLevel()))
                .collect(Collectors.joining("\n"));

        SkillGapResponse aiResult = aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(String.join(", ", currentSkills), candidate.headline(), jobsBlock),
                SkillGapResponse.class);

        log.info("Generated skill gap analysis for candidateId={}", candidateId);

        return new SkillGapResponse(currentSkills, aiResult.missingSkills(), aiResult.careerSuggestions());
    }
}

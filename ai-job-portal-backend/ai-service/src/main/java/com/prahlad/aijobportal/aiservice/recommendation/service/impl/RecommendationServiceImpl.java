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
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.MatchBreakdownResponse;
import com.prahlad.aijobportal.aiservice.recommendation.entity.JobRecommendation;
import com.prahlad.aijobportal.aiservice.recommendation.repository.JobRecommendationRepository;
import com.prahlad.aijobportal.aiservice.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DAY10 (AI Enhancement & ATS Intelligence): the recommendation engine
 * now asks Gemini for an explainable, six-dimension match breakdown
 * (Skill / Experience / Education / Project / Salary / Location) behind
 * every overall match score, instead of a single opaque percentage plus
 * one freeform sentence. Candidate and job context passed to the prompt
 * was widened accordingly (dated experience/education, salary range,
 * locations) — see the widened feign DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private static final String DIMENSION_SCHEMA = """
            For EACH item, also compute six 0-100 dimension scores that explain the \
            overall matchScore (matchScore should be a sensible weighted combination \
            of these, not an independent guess):
            - skillMatch: overlap between the candidate's skills and the job's required \
              skills, weighting mandatory skills heavily.
            - experienceMatch: how well the candidate's total years and seniority of \
              experience fit the job's experience level.
            - educationMatch: how well the candidate's highest/most relevant degree and \
              field of study fit the job's stated qualifications (if the job states \
              none, score generously).
            - projectMatch: how relevant the candidate's described projects/work \
              (from experience descriptions and summary) are to the job's domain.
            - salaryMatch: if the job discloses a salary range, how reasonable it is \
              for the candidate's apparent seniority (if no range is disclosed, or no \
              candidate expectation is known, score 70 as neutral).
            - locationMatch: fit between the candidate's location and the job's \
              locations/work mode (remote jobs should score high regardless of \
              candidate location).

            Also return "reasoning": an array of 3-6 short, specific, explainable \
            bullet strings (e.g. "Strong Java experience", "Spring Boot matches \
            requirement", "Missing Kafka experience", "Salary expectation slightly \
            higher") that justify the scores above. Do not return a single paragraph; \
            return a real array of short bullets.""";

    private static final String JOB_RECOMMENDATION_PROMPT = """
            You are a job matching assistant. Given a candidate's profile and a pool
            of open jobs, rank the jobs that best fit the candidate. Return a JSON
            object with a single field "recommendations": an array of objects, each
            with "id" (the exact jobId string as given below, unmodified) and
            "matchScore" (integer 0-100). Include only jobs that are a reasonable
            fit; omit poor matches entirely. Order the array from best match to worst.

            %s

            %s

            Candidate profile:
            %s

            Open jobs (id | title | company | type | experience level | work mode | salary | cities):
            %s
            """;

    private static final String HIRING_SIGNAL_SCHEMA = """
            Also return, for each item:
            - strengths: an array of 2-5 short strings naming this candidate's strongest points for this specific job.
            - weaknesses: an array of 1-5 short strings naming this candidate's gaps or concerns for this specific job (empty array if none).
            - missingSkills: an array of short strings naming required/preferred skills from the job that this candidate's profile does not show.
            - hiringRecommendation: exactly one of "Strongly Recommend", "Recommend", "Consider", or "Not a Fit", reflecting the overall matchScore and the strengths/weaknesses above.""";

    private static final String CANDIDATE_RECOMMENDATION_PROMPT = """
            You are a recruiting assistant helping rank applicants for a job. Given
            the job description and a pool of applicants, rank the applicants by
            fit. Return a JSON object with a single field "recommendations": an
            array of objects, each with "id" (the exact applicationId string as
            given below, unmodified) and "matchScore" (integer 0-100). Order the
            array from best match to worst.

            %s

            %s

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
                .map(job -> "%s | %s | %s | %s | %s | %s | %s | %s".formatted(
                        job.id(), job.title(), job.companyName(), job.jobType(), job.experienceLevel(),
                        job.workMode(), describeSalary(job.minSalary(), job.maxSalary(), job.currency(), null),
                        job.cities() == null ? "" : String.join(", ", job.cities())))
                .collect(Collectors.joining("\n"));

        RecommendationAiResult aiResult = aiStructuredResponseService.generateStructured(
                JOB_RECOMMENDATION_PROMPT.formatted(UntrustedTextGuard.INSTRUCTION, DIMENSION_SCHEMA,
                        UntrustedTextGuard.wrap("CANDIDATE_PROFILE", describeCandidate(candidate)),
                        UntrustedTextGuard.wrap("OPEN_JOBS", jobsBlock)),
                RecommendationAiResult.class);

        jobRecommendationRepository.deleteByCandidateId(candidateId);

        List<JobRecommendationResponse> responses = aiResult.recommendations().stream()
                .filter(item -> jobsById.containsKey(item.id()))
                .map(item -> {
                    JobLiteResponse job = jobsById.get(item.id());
                    MatchBreakdownResponse breakdown = clampBreakdown(item);
                    int clampedScore = clamp(item.matchScore());
                    List<String> reasoning = sanitizeReasoning(item.reasoning());

                    jobRecommendationRepository.save(JobRecommendation.builder()
                            .candidateId(candidateId)
                            .jobId(job.id())
                            .matchScore(clampedScore)
                            .skillMatch(breakdown.skillMatch())
                            .experienceMatch(breakdown.experienceMatch())
                            .educationMatch(breakdown.educationMatch())
                            .projectMatch(breakdown.projectMatch())
                            .salaryMatch(breakdown.salaryMatch())
                            .locationMatch(breakdown.locationMatch())
                            .reasoning(String.join("\n", reasoning))
                            .build());

                    return new JobRecommendationResponse(job.id(), job.title(), job.companyName(),
                            clampedScore, breakdown, reasoning);
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
                CANDIDATE_RECOMMENDATION_PROMPT.formatted(UntrustedTextGuard.INSTRUCTION, DIMENSION_SCHEMA, HIRING_SIGNAL_SCHEMA,
                        UntrustedTextGuard.wrap("JOB", describeJob(job)),
                        UntrustedTextGuard.wrap("APPLICANTS", applicantsBlock)),
                RecommendationAiResult.class);

        List<CandidateRecommendationResponse> responses = aiResult.recommendations().stream()
                .filter(item -> applicationsById.containsKey(item.id()))
                .map(item -> {
                    ApplicationSummaryResponse app = applicationsById.get(item.id());
                    MatchBreakdownResponse breakdown = clampBreakdown(item);
                    int clampedScore = clamp(item.matchScore());
                    return new CandidateRecommendationResponse(app.id(), app.candidateId(), app.candidateName(),
                            clampedScore, breakdown, sanitizeReasoning(item.reasoning()),
                            sanitizeReasoning(item.strengths()), sanitizeReasoning(item.weaknesses()),
                            sanitizeReasoning(item.missingSkills()), sanitizeHiringRecommendation(item.hiringRecommendation()));
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
                .map(s -> "%s (%s, %s yrs)".formatted(s.name(), s.proficiency(),
                        s.yearsOfExperience() == null ? "?" : s.yearsOfExperience()))
                .collect(Collectors.joining(", "));

        String experiences = candidate.experiences() == null ? "" : candidate.experiences().stream()
                .map(e -> "- %s at %s (%s): %s".formatted(
                        e.jobTitle(), e.companyName(), describeDuration(e.startDate(), e.endDate(), e.currentlyWorking()),
                        e.description() == null ? "" : e.description()))
                .collect(Collectors.joining("\n"));

        String educations = candidate.educations() == null ? "" : candidate.educations().stream()
                .map(ed -> "- %s in %s at %s (%s)".formatted(
                        ed.degreeType(), ed.fieldOfStudy(), ed.institutionName(),
                        ed.currentlyStudying() ? "ongoing" : String.valueOf(ed.endDate())))
                .collect(Collectors.joining("\n"));

        int totalYears = totalYearsOfExperience(candidate);

        return """
                Name: %s
                Headline: %s
                Summary: %s
                Location: %s, %s, %s
                Total years of experience (computed): %d
                Skills: %s
                Experience:
                %s
                Education:
                %s
                """.formatted(candidate.fullName(), candidate.headline(), candidate.summary(),
                candidate.city(), candidate.state(), candidate.country(), totalYears, skills, experiences, educations);
    }

    private String describeJob(JobDetailSummaryResponse job) {
        String skills = job.skills() == null ? "" : job.skills().stream()
                .map(s -> "%s (%s%s)".formatted(s.name(), s.requiredProficiency(), s.mandatory() ? ", mandatory" : ""))
                .collect(Collectors.joining(", "));

        String locations = job.locations() == null ? "" : job.locations().stream()
                .map(l -> "%s, %s, %s".formatted(l.city(), l.state(), l.country()))
                .collect(Collectors.joining(" | "));

        String requirements = job.requirements() == null ? "" : job.requirements().stream()
                .map(r -> "- [%s] %s".formatted(r.type(), r.description()))
                .collect(Collectors.joining("\n"));

        return """
                Title: %s
                Company: %s
                Type: %s
                Experience Level: %s
                Work Mode: %s
                Locations: %s
                Salary: %s
                Description: %s
                Required Skills: %s
                Requirements:
                %s
                """.formatted(job.title(), job.companyName(), job.jobType(), job.experienceLevel(), job.workMode(),
                locations, describeSalary(job.minSalary(), job.maxSalary(), job.currency(), job.salaryType()),
                job.description(), skills, requirements);
    }

    private String describeSalary(BigDecimal min, BigDecimal max, String currency, String salaryType) {
        if (min == null && max == null) {
            return "Not disclosed";
        }
        String range = min != null && max != null ? "%s - %s".formatted(min, max)
                : String.valueOf(min != null ? min : max);
        return "%s %s%s".formatted(range, currency == null ? "" : currency,
                salaryType == null ? "" : " / " + salaryType);
    }

    private String describeDuration(LocalDate start, LocalDate end, boolean currentlyWorking) {
        if (start == null) {
            return "duration unknown";
        }
        LocalDate effectiveEnd = currentlyWorking || end == null ? LocalDate.now() : end;
        Period period = Period.between(start, effectiveEnd);
        return "%s to %s, %d yr %d mo".formatted(start, currentlyWorking ? "present" : String.valueOf(end),
                period.getYears(), period.getMonths());
    }

    private int totalYearsOfExperience(CandidateProfileSummaryResponse candidate) {
        if (candidate.experiences() == null || candidate.experiences().isEmpty()) {
            return 0;
        }
        long totalMonths = candidate.experiences().stream()
                .filter(e -> e.startDate() != null)
                .mapToLong(e -> {
                    LocalDate end = e.currentlyWorking() || e.endDate() == null ? LocalDate.now() : e.endDate();
                    return ChronoUnit.MONTHS.between(e.startDate(), end);
                })
                .sum();
        return (int) (totalMonths / 12);
    }

    private MatchBreakdownResponse clampBreakdown(RecommendationAiResult.Item item) {
        return new MatchBreakdownResponse(
                clamp(item.skillMatch()), clamp(item.experienceMatch()), clamp(item.educationMatch()),
                clamp(item.projectMatch()), clamp(item.salaryMatch()), clamp(item.locationMatch()));
    }

    private List<String> sanitizeReasoning(List<String> reasoning) {
        if (reasoning == null) {
            return List.of();
        }
        return reasoning.stream()
                .filter(r -> r != null && !r.isBlank())
                .map(String::strip)
                .toList();
    }

    private static final List<String> VALID_HIRING_RECOMMENDATIONS =
            List.of("Strongly Recommend", "Recommend", "Consider", "Not a Fit");

    /**
     * Falls back to a score-derived label if Gemini returns something
     * outside the four allowed values (or nothing at all), so the API
     * never exposes an unbounded freeform string here.
     */
    private String sanitizeHiringRecommendation(String hiringRecommendation) {
        if (hiringRecommendation != null) {
            String stripped = hiringRecommendation.strip();
            for (String valid : VALID_HIRING_RECOMMENDATIONS) {
                if (valid.equalsIgnoreCase(stripped)) {
                    return valid;
                }
            }
        }
        return "Consider";
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }
}

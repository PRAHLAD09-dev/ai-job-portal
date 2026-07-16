package com.prahlad.aijobportal.recruiterservice.dashboard.service.impl;

import com.prahlad.aijobportal.recruiterservice.dashboard.dto.response.RecentApplicationInsightResponse;
import com.prahlad.aijobportal.recruiterservice.dashboard.dto.response.RecruiterDashboardResponse;
import com.prahlad.aijobportal.recruiterservice.dashboard.service.RecruiterDashboardService;
import com.prahlad.aijobportal.recruiterservice.feign.AiServiceClient;
import com.prahlad.aijobportal.recruiterservice.feign.ApplicationServiceClient;
import com.prahlad.aijobportal.recruiterservice.feign.JobServiceClient;
import com.prahlad.aijobportal.recruiterservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.ApplicationSummaryResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.JobSavedCountResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.JobStatisticsResponse;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregates the DAY11 Recruiter Dashboard purely by calling existing
 * APIs on Job Service, Application Service, and AI Service through
 * Feign — no new data is stored here, and no downstream service's
 * business logic is duplicated.
 *
 * <p>The "recent applications" AI Match lookup is N+1 by design: it
 * only runs over one page (the dashboard's most recent applications,
 * not the company's entire history), and a missing analysis (404) is
 * treated as "no AI match yet" rather than an error, so one candidate
 * without a resume analysis never fails the whole dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterDashboardServiceImpl implements RecruiterDashboardService {

    private static final int RECENT_APPLICATIONS_PAGE_SIZE = 10;

    private final JobServiceClient jobServiceClient;
    private final ApplicationServiceClient applicationServiceClient;
    private final AiServiceClient aiServiceClient;

    @Override
    public RecruiterDashboardResponse getDashboard(String bearerToken) {
        JobStatisticsResponse jobStatistics = unwrap(jobServiceClient.getMyCompanyStatistics(bearerToken));
        List<JobSavedCountResponse> savedJobStatistics = unwrap(jobServiceClient.getMyCompanySavedJobStatistics(bearerToken));
        ApplicationStatisticsResponse applicationStatistics = unwrap(applicationServiceClient.getStatistics(bearerToken));

        List<ApplicationSummaryResponse> recentApplications =
                unwrap(applicationServiceClient.getApplications(bearerToken, 0, RECENT_APPLICATIONS_PAGE_SIZE)).getContent();

        Map<UUID, Integer> aiMatchByCandidate = fetchAiMatchScores(recentApplications);

        List<RecentApplicationInsightResponse> insights = recentApplications.stream()
                .map(app -> new RecentApplicationInsightResponse(
                        app.id(), app.candidateId(), app.candidateName(), app.jobId(), app.jobTitle(),
                        app.status(), app.appliedAt(), app.viewed(), aiMatchByCandidate.get(app.candidateId())))
                .toList();

        return new RecruiterDashboardResponse(jobStatistics, applicationStatistics, savedJobStatistics, insights);
    }

    /**
     * Looks up each distinct candidate's latest AI Match score at most
     * once, even if they applied to more than one of the recruiter's
     * jobs in this page.
     */
    private Map<UUID, Integer> fetchAiMatchScores(List<ApplicationSummaryResponse> applications) {
        Map<UUID, Integer> scores = new LinkedHashMap<>();
        for (ApplicationSummaryResponse app : applications) {
            UUID candidateId = app.candidateId();
            if (scores.containsKey(candidateId)) {
                continue;
            }
            try {
                scores.put(candidateId, unwrap(aiServiceClient.getLatestResumeAnalysis(candidateId)).atsScore());
            } catch (ResourceNotFoundException notYetAnalyzed) {
                scores.put(candidateId, null);
            } catch (Exception ex) {
                // AI Service being briefly unavailable shouldn't fail the
                // whole dashboard - the candidate's AI Match simply shows
                // as unavailable for this refresh.
                log.warn("Could not fetch AI Match for candidateId={}: {}", candidateId, ex.getMessage());
                scores.put(candidateId, null);
            }
        }
        return scores;
    }

    private static <T> T unwrap(ApiResponse<T> response) {
        return response.getData();
    }
}

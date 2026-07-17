package com.prahlad.aijobportal.adminservice.dashboard.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.dashboard.dto.response.ChartDataPoint;
import com.prahlad.aijobportal.adminservice.dashboard.dto.response.DashboardChartsResponse;
import com.prahlad.aijobportal.adminservice.dashboard.dto.response.DashboardResponse;
import com.prahlad.aijobportal.adminservice.dashboard.service.DashboardService;
import com.prahlad.aijobportal.adminservice.feign.AiServiceClient;
import com.prahlad.aijobportal.adminservice.feign.ApplicationServiceClient;
import com.prahlad.aijobportal.adminservice.feign.AuthServiceClient;
import com.prahlad.aijobportal.adminservice.feign.JobServiceClient;
import com.prahlad.aijobportal.adminservice.feign.NotificationServiceClient;
import com.prahlad.aijobportal.adminservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.AiStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyStatisticsResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.JobStatisticsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Fetches every platform statistic from its owning service and combines
 * it with this service's own recent-activity audit trail, per
 * DAY09_ADMIN_SERVICE.md's Dashboard section. Never duplicates any
 * downstream service's counting logic — each number comes straight from
 * that service's own internal admin statistics endpoint.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private static final int RECENT_ACTIVITY_LIMIT = 10;

    private final AuthServiceClient authServiceClient;
    private final RecruiterServiceClient recruiterServiceClient;
    private final JobServiceClient jobServiceClient;
    private final ApplicationServiceClient applicationServiceClient;
    private final AiServiceClient aiServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    private final AuditLogService auditLogService;

    @Override
    @CircuitBreaker(name = "dashboard", fallbackMethod = "getDashboardFallback")
    public DashboardResponse getDashboard() {
        var recentActivity = auditLogService.getRecentActivity(
                PageRequest.of(0, RECENT_ACTIVITY_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")));

        return new DashboardResponse(
                authServiceClient.getStatistics().getData(),
                recruiterServiceClient.getStatistics().getData(),
                jobServiceClient.getStatistics().getData(),
                applicationServiceClient.getStatistics().getData(),
                aiServiceClient.getStatistics().getData(),
                notificationServiceClient.getStatistics().getData(),
                recentActivity.getContent()
        );
    }

    /**
     * If any single downstream statistics call fails, the whole
     * dashboard still degrades gracefully rather than throwing a 502 for
     * the entire page — every section the admin sees is either real data
     * or explicitly null, never a stale/fabricated number.
     */
    @SuppressWarnings("unused")
    private DashboardResponse getDashboardFallback(Throwable throwable) {
        log.error("Dashboard aggregation failed; returning partial/empty dashboard", throwable);
        var recentActivity = auditLogService.getRecentActivity(
                PageRequest.of(0, RECENT_ACTIVITY_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")));

        return new DashboardResponse(null, null, null, null, null, null, recentActivity.getContent());
    }

    @Override
    @CircuitBreaker(name = "dashboard", fallbackMethod = "getDashboardChartsFallback")
    public DashboardChartsResponse getDashboardCharts(int userGrowthDays) {
        CompanyStatisticsResponse companyStats = recruiterServiceClient.getStatistics().getData();
        JobStatisticsResponse jobStats = jobServiceClient.getStatistics().getData();
        ApplicationStatisticsResponse applicationStats = applicationServiceClient.getStatistics().getData();
        AiStatisticsResponse aiStats = aiServiceClient.getStatistics().getData();

        return new DashboardChartsResponse(
                authServiceClient.getUserGrowth(userGrowthDays).getData(),
                companyVerificationBreakdown(companyStats),
                jobsByStatusBreakdown(jobStats),
                applicationsByStatusBreakdown(applicationStats),
                aiUsageBreakdown(aiStats)
        );
    }

    @SuppressWarnings("unused")
    private DashboardChartsResponse getDashboardChartsFallback(int userGrowthDays, Throwable throwable) {
        log.error("Dashboard charts aggregation failed; returning empty charts", throwable);
        return new DashboardChartsResponse(List.of(), List.of(), List.of(), List.of(), List.of());
    }

    private List<ChartDataPoint> companyVerificationBreakdown(CompanyStatisticsResponse stats) {
        if (stats == null) {
            return List.of();
        }
        return List.of(
                new ChartDataPoint("Pending", stats.pendingCompanies()),
                new ChartDataPoint("Verified", stats.verifiedCompanies()),
                new ChartDataPoint("Rejected", stats.rejectedCompanies()),
                new ChartDataPoint("Suspended", stats.suspendedCompanies())
        );
    }

    private List<ChartDataPoint> jobsByStatusBreakdown(JobStatisticsResponse stats) {
        if (stats == null) {
            return List.of();
        }
        return List.of(
                new ChartDataPoint("Draft", stats.draftJobs()),
                new ChartDataPoint("Published", stats.publishedJobs()),
                new ChartDataPoint("Closed", stats.closedJobs()),
                new ChartDataPoint("Archived", stats.archivedJobs())
        );
    }

    private List<ChartDataPoint> applicationsByStatusBreakdown(ApplicationStatisticsResponse stats) {
        if (stats == null) {
            return List.of();
        }
        return List.of(
                new ChartDataPoint("Applied", stats.appliedCount()),
                new ChartDataPoint("Under Review", stats.underReviewCount()),
                new ChartDataPoint("Shortlisted", stats.shortlistedCount()),
                new ChartDataPoint("Interview", stats.interviewCount()),
                new ChartDataPoint("Offered", stats.offeredCount()),
                new ChartDataPoint("Hired", stats.hiredCount()),
                new ChartDataPoint("Rejected", stats.rejectedCount()),
                new ChartDataPoint("Withdrawn", stats.withdrawnCount())
        );
    }

    private List<ChartDataPoint> aiUsageBreakdown(AiStatisticsResponse stats) {
        if (stats == null) {
            return List.of();
        }
        return List.of(
                new ChartDataPoint("Resume Analyses", stats.totalResumeAnalyses()),
                new ChartDataPoint("Job Recommendations", stats.totalJobRecommendations()),
                new ChartDataPoint("Interview Question Sets", stats.totalInterviewQuestionSets())
        );
    }
}

package com.prahlad.aijobportal.jobservice.job.service;

import com.prahlad.aijobportal.jobservice.job.dto.request.CreateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.request.JobSearchCriteria;
import com.prahlad.aijobportal.jobservice.job.dto.request.UpdateJobRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSavedCountResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobStatisticsResponse;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobSummaryResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface JobService {

    JobResponse createJob(UUID userId, String bearerToken, CreateJobRequest request);
    JobResponse updateJob(UUID userId, String bearerToken, UUID jobId, UpdateJobRequest request);
    void deleteJob(UUID userId, String bearerToken, UUID jobId);
    JobResponse publishJob(UUID userId, String bearerToken, UUID jobId);
    JobResponse closeJob(UUID userId, String bearerToken, UUID jobId);
    JobResponse reopenJob(UUID userId, String bearerToken, UUID jobId);
    JobResponse duplicateJob(UUID userId, String bearerToken, UUID jobId);
    JobResponse previewJob(UUID userId, String bearerToken, UUID jobId);
    PageResponse<JobSummaryResponse> getMyCompanyJobs(UUID userId, String bearerToken, Pageable pageable);
    JobStatisticsResponse getMyCompanyStatistics(UUID userId, String bearerToken);

    /**
     * DAY11 Recruiter Dashboard "Saved Job Statistics": per-job saved
     * (bookmark) counts across the authenticated recruiter's company.
     */
    List<JobSavedCountResponse> getMyCompanySavedJobStatistics(UUID userId, String bearerToken);

    JobResponse getJobById(UUID jobId);
    JobResponse getJobBySlug(String slug);
    PageResponse<JobSummaryResponse> searchJobs(JobSearchCriteria criteria, Pageable pageable);
    List<JobSummaryResponse> getLatestJobs();
    List<JobSummaryResponse> getFeaturedJobs();
    List<JobSummaryResponse> getTrendingJobs();
    List<JobSummaryResponse> getSimilarJobs(UUID jobId);
}

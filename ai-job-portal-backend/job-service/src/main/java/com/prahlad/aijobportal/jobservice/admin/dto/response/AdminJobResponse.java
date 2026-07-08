package com.prahlad.aijobportal.jobservice.admin.dto.response;

import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;

import java.time.Instant;
import java.util.UUID;

/**
 * Job projection returned by Job Service's internal admin endpoints,
 * consumed exclusively by Admin Service via Feign. Kept separate from the
 * candidate/recruiter-facing job response records since Admin Service
 * only needs listing-level fields for its moderation views.
 */
public record AdminJobResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String title,
        String slug,
        JobType jobType,
        WorkMode workMode,
        JobStatus status,
        boolean featured,
        long viewCount,
        Instant publishedAt,
        Instant closedAt,
        Instant createdAt
) {
}

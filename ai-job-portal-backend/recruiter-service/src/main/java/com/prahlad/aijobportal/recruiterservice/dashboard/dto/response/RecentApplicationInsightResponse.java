package com.prahlad.aijobportal.recruiterservice.dashboard.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * One row of the Recruiter Dashboard's "recent applications" table:
 * Application Service's application summary (including its {@code
 * viewed} status) joined in-memory with AI Service's AI Match score for
 * that same candidate. {@code aiMatchScore} is {@code null} when the
 * candidate has no resume analysis yet — this is a normal, expected
 * state, not an error.
 */
public record RecentApplicationInsightResponse(
        UUID applicationId,
        UUID candidateId,
        String candidateName,
        UUID jobId,
        String jobTitle,
        String status,
        Instant appliedAt,
        boolean viewed,
        Integer aiMatchScore
) {
}

package com.prahlad.aijobportal.jobservice.job.enums;

/**
 * Lifecycle status of a {@code Job}. Only {@code PUBLISHED} jobs are
 * visible to candidates via the public APIs, per
 * PROJECT_SPECIFICATION.md Section 16 (Business Rules): "Only published
 * jobs are visible to candidates. Archived jobs are hidden."
 */
public enum JobStatus {
    DRAFT,
    PUBLISHED,
    CLOSED,
    ARCHIVED
}

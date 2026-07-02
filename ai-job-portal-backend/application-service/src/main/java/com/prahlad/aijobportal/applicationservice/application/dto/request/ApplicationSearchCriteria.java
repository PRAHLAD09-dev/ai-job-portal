package com.prahlad.aijobportal.applicationservice.application.dto.request;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Encapsulates every optional filter/search parameter accepted by the
 * recruiter-facing {@code GET /recruiter/applications} endpoint (per
 * DAY06's Recruiter "Filter Applications" / "Search Applications"
 * features), bound from individual {@code @RequestParam} values. All
 * fields are optional; {@code null} means "no filter on this
 * dimension". Always additionally constrained to the caller's own
 * {@code companyId} in the service layer.
 */
public record ApplicationSearchCriteria(
        String keyword,
        UUID jobId,
        ApplicationStatus status,
        Instant appliedAfter,
        Instant appliedBefore
) {
}

package com.prahlad.aijobportal.adminservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an administrator verifies a company, per
 * DAY09_ADMIN_SERVICE.md's Kafka section.
 */
public record CompanyVerifiedEvent(
        UUID companyId,
        String companyName,
        UUID verifiedByAdminId,
        Instant verifiedAt
) {
}

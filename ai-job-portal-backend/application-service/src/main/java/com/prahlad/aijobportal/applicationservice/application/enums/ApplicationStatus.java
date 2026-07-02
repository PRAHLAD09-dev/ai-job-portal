package com.prahlad.aijobportal.applicationservice.application.enums;

/**
 * Lifecycle status of a {@link com.prahlad.aijobportal.applicationservice.application.entity.JobApplication}.
 * Terminal states are {@code HIRED}, {@code REJECTED}, and
 * {@code WITHDRAWN} — no further transitions are allowed once an
 * application reaches one of these, per DAY06_APPLICATION_SERVICE.md's
 * Validation section ("Cannot update completed application").
 */
public enum ApplicationStatus {
    APPLIED,
    UNDER_REVIEW,
    SHORTLISTED,
    INTERVIEW,
    OFFERED,
    HIRED,
    REJECTED,
    WITHDRAWN;

    public boolean isTerminal() {
        return this == HIRED || this == REJECTED || this == WITHDRAWN;
    }
}

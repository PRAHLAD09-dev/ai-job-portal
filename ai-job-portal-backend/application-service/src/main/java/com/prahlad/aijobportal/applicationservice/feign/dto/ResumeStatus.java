package com.prahlad.aijobportal.applicationservice.feign.dto;

/**
 * Mirrors Candidate Service's {@code ResumeStatus} (exactly one resume
 * per candidate is ACTIVE at a time; older versions are ARCHIVED). Per
 * DECISIONS.md, services don't share entity/enum types directly across
 * the Feign boundary - this is application-service's own minimal copy
 * of just the values it needs to deterministically resolve "the
 * candidate's current resume" without depending on collection order.
 */
public enum ResumeStatus {
    ACTIVE,
    ARCHIVED
}

package com.prahlad.aijobportal.candidateservice.resume.enums;

/**
 * Lifecycle status of a candidate's uploaded resume. Exactly one resume
 * per candidate may be ACTIVE (the one used by default when applying to
 * jobs); older versions become ARCHIVED rather than being deleted, to
 * preserve resume version history.
 */
public enum ResumeStatus {
    ACTIVE,
    ARCHIVED
}

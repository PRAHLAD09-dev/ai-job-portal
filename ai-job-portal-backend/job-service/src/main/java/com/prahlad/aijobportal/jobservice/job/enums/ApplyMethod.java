package com.prahlad.aijobportal.jobservice.job.enums;

/**
 * DAY11 "Apply Methods": the recruiter chooses, per job, how a
 * candidate applies. Drives Application Service's candidate flow
 * (EASY_APPLY / QUICK_APPLY) and the frontend's external redirect
 * (EXTERNAL_APPLY).
 */
public enum ApplyMethod {

    /** Standard in-app application flow: candidate picks a resume and writes a cover letter. */
    EASY_APPLY,

    /** In-app flow with automatic resume selection — candidate's active resume is used, no picker shown. */
    QUICK_APPLY,

    /** No in-app application is created; the candidate is redirected to {@code externalApplyUrl}. */
    EXTERNAL_APPLY
}

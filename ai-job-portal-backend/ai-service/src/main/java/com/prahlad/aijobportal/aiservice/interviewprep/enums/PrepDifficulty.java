package com.prahlad.aijobportal.aiservice.interviewprep.enums;

/**
 * Difficulty level for a candidate-generated interview practice set,
 * per the AI Interview Generator PRD's "Difficulty" step. Deliberately
 * a separate enum from {@code interview.enums.QuestionDifficulty}
 * (used by the recruiter-facing, job-based interview question bank)
 * even though the values are identical - the two features are
 * different bounded contexts (candidate resume practice vs recruiter
 * job-based question bank) generated from different inputs, and
 * keeping them decoupled avoids one feature's evolution (e.g. adding
 * an EXPERT tier here) forcing a change on the other.
 */
public enum PrepDifficulty {
    EASY,
    MEDIUM,
    HARD
}

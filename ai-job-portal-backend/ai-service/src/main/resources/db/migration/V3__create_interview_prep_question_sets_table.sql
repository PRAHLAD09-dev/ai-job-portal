-- AI Interview Generator PRD: candidate-facing, resume-based interview
-- practice question sets. Distinct from interview_questions (V1), which
-- holds recruiter-facing, job-based interview questions.

CREATE TABLE interview_prep_question_sets (
    id                  UUID PRIMARY KEY,
    candidate_id        UUID        NOT NULL,
    resume_analysis_id  UUID        NOT NULL,
    selected_topics     TEXT        NOT NULL,
    difficulty          VARCHAR(20) NOT NULL,
    question_type       VARCHAR(20) NOT NULL,
    question_count      INTEGER     NOT NULL,
    questions_json      TEXT        NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_interview_prep_candidate ON interview_prep_question_sets (candidate_id);

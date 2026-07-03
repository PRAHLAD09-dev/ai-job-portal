-- V1__create_ai_service_tables.sql
-- Tables owned by AI Service, per DAY07_AI_SERVICE.md's "Flyway" and
-- "Database" sections: resume_analysis, job_recommendations,
-- interview_questions.

CREATE TABLE resume_analysis (
    id                UUID PRIMARY KEY,
    candidate_id      UUID         NOT NULL,
    resume_url        VARCHAR(1000) NOT NULL,
    resume_text       TEXT         NOT NULL,
    resume_text_hash  VARCHAR(64)  NOT NULL,
    ats_score         INTEGER      NOT NULL,
    strengths         TEXT,
    weaknesses        TEXT,
    missing_skills    TEXT,
    recommendations   TEXT,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_resume_analysis_candidate ON resume_analysis (candidate_id);
CREATE INDEX idx_resume_analysis_resume_hash ON resume_analysis (candidate_id, resume_text_hash);

CREATE TABLE job_recommendations (
    id             UUID PRIMARY KEY,
    candidate_id   UUID        NOT NULL,
    job_id         UUID        NOT NULL,
    match_score    INTEGER     NOT NULL,
    reasoning      TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_job_recommendation_candidate_job UNIQUE (candidate_id, job_id)
);

CREATE INDEX idx_job_recommendation_candidate ON job_recommendations (candidate_id);

CREATE TABLE interview_questions (
    id           UUID PRIMARY KEY,
    job_id       UUID        NOT NULL,
    question     TEXT        NOT NULL,
    difficulty   VARCHAR(20) NOT NULL,
    category     VARCHAR(30) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_interview_question_job ON interview_questions (job_id);

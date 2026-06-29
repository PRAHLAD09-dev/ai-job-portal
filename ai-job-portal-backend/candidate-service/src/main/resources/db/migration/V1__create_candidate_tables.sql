-- V1__create_candidate_tables.sql
-- Initial schema for the Candidate Service: candidates, educations,
-- experiences, skills, resumes. Owns ONLY candidate-profile data, per
-- PROJECT_SPECIFICATION.md Section 18 (Module Boundaries). The
-- candidates.user_id column references the Auth Service's User.id by
-- VALUE ONLY — no foreign key constraint, since cross-service direct
-- database access/FKs are forbidden per DECISIONS.md.

CREATE TABLE candidates (
    id                              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                         UUID NOT NULL,
    email                           VARCHAR(255) NOT NULL,
    full_name                       VARCHAR(200) NOT NULL,
    headline                        VARCHAR(200),
    summary                         TEXT,
    phone_number                    VARCHAR(20),
    date_of_birth                   DATE,
    city                            VARCHAR(100),
    state                           VARCHAR(100),
    country                         VARCHAR(100),
    portfolio_url                   VARCHAR(500),
    linkedin_url                    VARCHAR(500),
    github_url                      VARCHAR(500),
    visibility                      VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    profile_completion_percentage   INTEGER NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_candidates_user_id UNIQUE (user_id)
);

CREATE INDEX idx_candidates_user_id ON candidates (user_id);

CREATE TABLE educations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id        UUID NOT NULL,
    institution_name    VARCHAR(200) NOT NULL,
    degree_type         VARCHAR(30) NOT NULL,
    field_of_study      VARCHAR(150) NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE,
    currently_studying  BOOLEAN NOT NULL DEFAULT FALSE,
    grade               VARCHAR(50),
    description         TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_educations_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id) ON DELETE CASCADE
);

CREATE INDEX idx_educations_candidate_id ON educations (candidate_id);

CREATE TABLE experiences (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id        UUID NOT NULL,
    company_name        VARCHAR(200) NOT NULL,
    job_title           VARCHAR(150) NOT NULL,
    employment_type      VARCHAR(30) NOT NULL,
    location            VARCHAR(150),
    start_date          DATE NOT NULL,
    end_date            DATE,
    currently_working   BOOLEAN NOT NULL DEFAULT FALSE,
    description         TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_experiences_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id) ON DELETE CASCADE
);

CREATE INDEX idx_experiences_candidate_id ON experiences (candidate_id);

CREATE TABLE skills (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id         UUID NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    proficiency          VARCHAR(30) NOT NULL,
    years_of_experience  INTEGER,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_skills_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id) ON DELETE CASCADE,
    CONSTRAINT uk_skills_candidate_name UNIQUE (candidate_id, name)
);

CREATE INDEX idx_skills_candidate_id ON skills (candidate_id);

CREATE TABLE resumes (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id            UUID NOT NULL,
    file_name               VARCHAR(255) NOT NULL,
    file_url                VARCHAR(1000) NOT NULL,
    cloudinary_public_id    VARCHAR(500) NOT NULL,
    file_format             VARCHAR(20) NOT NULL,
    file_size_bytes         BIGINT NOT NULL,
    version_number          INTEGER NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_resumes_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id) ON DELETE CASCADE
);

CREATE INDEX idx_resumes_candidate_id ON resumes (candidate_id);
CREATE INDEX idx_resumes_candidate_status ON resumes (candidate_id, status);

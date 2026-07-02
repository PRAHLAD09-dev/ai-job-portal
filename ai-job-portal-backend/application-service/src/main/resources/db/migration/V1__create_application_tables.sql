-- V1__create_application_tables.sql
-- Creates the applications and application_timeline tables owned
-- exclusively by the Application Service (application_service_db), per
-- DAY06_APPLICATION_SERVICE.md and DECISIONS.md ("no shared database").

CREATE TABLE applications (
    id                 UUID PRIMARY KEY,
    candidate_id       UUID NOT NULL,
    candidate_user_id  UUID NOT NULL,
    candidate_name     VARCHAR(200) NOT NULL,
    candidate_email    VARCHAR(200) NOT NULL,
    recruiter_id       UUID,
    recruiter_user_id  UUID,
    company_id         UUID NOT NULL,
    company_name       VARCHAR(200) NOT NULL,
    job_id             UUID NOT NULL,
    job_title          VARCHAR(200) NOT NULL,
    resume_url         VARCHAR(1000) NOT NULL,
    cover_letter       TEXT,
    status             VARCHAR(20) NOT NULL,
    applied_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    interview_date     TIMESTAMP WITHOUT TIME ZONE,
    notes              TEXT,
    withdrawn_at       TIMESTAMP WITHOUT TIME ZONE,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT uk_application_job_candidate UNIQUE (job_id, candidate_id)
);

CREATE INDEX idx_application_candidate_user ON applications (candidate_user_id);
CREATE INDEX idx_application_company ON applications (company_id);
CREATE INDEX idx_application_job ON applications (job_id);
CREATE INDEX idx_application_status ON applications (status);

CREATE TABLE application_timeline (
    id             UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    old_status     VARCHAR(20),
    new_status     VARCHAR(20) NOT NULL,
    changed_by     UUID NOT NULL,
    remarks        TEXT,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_timeline_application FOREIGN KEY (application_id) REFERENCES applications (id)
);

CREATE INDEX idx_timeline_application ON application_timeline (application_id);

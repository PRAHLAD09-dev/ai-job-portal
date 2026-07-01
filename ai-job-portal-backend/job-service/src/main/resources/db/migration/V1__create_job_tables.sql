-- V1__create_job_tables.sql
-- Initial schema for the Job Service: job_categories, jobs, job_skills,
-- job_benefits, job_locations, job_requirements, saved_jobs, job_alerts.
-- Owns ONLY job-listing data, per PROJECT_SPECIFICATION.md Section 18
-- (Module Boundaries) — never applications or interviews. jobs.company_id /
-- jobs.recruiter_id and saved_jobs.user_id / job_alerts.user_id reference
-- Recruiter Service / Auth Service records by VALUE ONLY — no foreign key
-- constraint, since cross-service direct database access/FKs are
-- forbidden per DECISIONS.md.

CREATE TABLE job_categories (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                 VARCHAR(100) NOT NULL,
    slug                 VARCHAR(120) NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_job_categories_name UNIQUE (name),
    CONSTRAINT uk_job_categories_slug UNIQUE (slug)
);

CREATE TABLE jobs (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id           UUID NOT NULL,
    recruiter_id         UUID NOT NULL,
    company_name         VARCHAR(200) NOT NULL,
    company_logo_url     VARCHAR(1000),
    category_id          UUID NOT NULL,
    title                VARCHAR(200) NOT NULL,
    slug                 VARCHAR(240) NOT NULL,
    description          TEXT NOT NULL,
    job_type             VARCHAR(30) NOT NULL,
    experience_level     VARCHAR(30) NOT NULL,
    work_mode            VARCHAR(20) NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    min_salary           NUMERIC(14, 2),
    max_salary           NUMERIC(14, 2),
    salary_type          VARCHAR(20),
    currency             VARCHAR(10),
    vacancies            INTEGER NOT NULL DEFAULT 1,
    application_deadline TIMESTAMP WITH TIME ZONE,
    featured             BOOLEAN NOT NULL DEFAULT FALSE,
    view_count           BIGINT NOT NULL DEFAULT 0,
    published_at         TIMESTAMP WITH TIME ZONE,
    closed_at            TIMESTAMP WITH TIME ZONE,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_jobs_slug UNIQUE (slug),
    CONSTRAINT fk_jobs_category FOREIGN KEY (category_id) REFERENCES job_categories (id)
);

CREATE INDEX idx_jobs_company_id ON jobs (company_id);
CREATE INDEX idx_jobs_status ON jobs (status);
CREATE INDEX idx_jobs_category_id ON jobs (category_id);
CREATE INDEX idx_jobs_status_published_at ON jobs (status, published_at DESC);
CREATE INDEX idx_jobs_status_featured ON jobs (status, featured);
CREATE INDEX idx_jobs_view_count ON jobs (view_count DESC);

CREATE TABLE job_skills (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id                UUID NOT NULL,
    name                  VARCHAR(100) NOT NULL,
    required_proficiency  VARCHAR(30) NOT NULL,
    mandatory             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_job_skills_job FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE,
    CONSTRAINT uk_job_skills_job_name UNIQUE (job_id, name)
);

CREATE INDEX idx_job_skills_name ON job_skills (name);
CREATE INDEX idx_job_skills_job_id ON job_skills (job_id);

CREATE TABLE job_benefits (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id                UUID NOT NULL,
    title                 VARCHAR(150) NOT NULL,
    description           VARCHAR(500),
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_job_benefits_job FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE
);

CREATE INDEX idx_job_benefits_job_id ON job_benefits (job_id);

CREATE TABLE job_locations (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id                UUID NOT NULL,
    city                  VARCHAR(100) NOT NULL,
    state                 VARCHAR(100),
    country               VARCHAR(100) NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_job_locations_job FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE
);

CREATE INDEX idx_job_locations_job_id ON job_locations (job_id);
CREATE INDEX idx_job_locations_city ON job_locations (city);
CREATE INDEX idx_job_locations_state ON job_locations (state);
CREATE INDEX idx_job_locations_country ON job_locations (country);

CREATE TABLE job_requirements (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id                UUID NOT NULL,
    type                  VARCHAR(30) NOT NULL,
    description           VARCHAR(1000) NOT NULL,
    display_order         INTEGER NOT NULL DEFAULT 0,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_job_requirements_job FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE
);

CREATE INDEX idx_job_requirements_job_id ON job_requirements (job_id);

CREATE TABLE saved_jobs (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL,
    job_id                UUID NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_saved_jobs_job FOREIGN KEY (job_id) REFERENCES jobs (id) ON DELETE CASCADE,
    CONSTRAINT uk_saved_jobs_user_job UNIQUE (user_id, job_id)
);

CREATE INDEX idx_saved_jobs_user_id ON saved_jobs (user_id);

CREATE TABLE job_alerts (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL,
    keyword               VARCHAR(200),
    category_id           UUID,
    job_type              VARCHAR(30),
    experience_level      VARCHAR(30),
    work_mode             VARCHAR(20),
    city                  VARCHAR(100),
    frequency             VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_job_alerts_category FOREIGN KEY (category_id) REFERENCES job_categories (id)
);

CREATE INDEX idx_job_alerts_user_id ON job_alerts (user_id);

-- Seed reference data: job categories (per DAY05, seeded via Flyway like
-- Role was in auth-service).
INSERT INTO job_categories (id, name, slug) VALUES
    (gen_random_uuid(), 'Engineering', 'engineering'),
    (gen_random_uuid(), 'Product Management', 'product-management'),
    (gen_random_uuid(), 'Design', 'design'),
    (gen_random_uuid(), 'Marketing', 'marketing'),
    (gen_random_uuid(), 'Sales', 'sales'),
    (gen_random_uuid(), 'Customer Support', 'customer-support'),
    (gen_random_uuid(), 'Human Resources', 'human-resources'),
    (gen_random_uuid(), 'Finance', 'finance'),
    (gen_random_uuid(), 'Operations', 'operations'),
    (gen_random_uuid(), 'Data Science', 'data-science'),
    (gen_random_uuid(), 'DevOps & Infrastructure', 'devops-infrastructure'),
    (gen_random_uuid(), 'Quality Assurance', 'quality-assurance'),
    (gen_random_uuid(), 'Legal', 'legal'),
    (gen_random_uuid(), 'Content & Writing', 'content-writing'),
    (gen_random_uuid(), 'Other', 'other');

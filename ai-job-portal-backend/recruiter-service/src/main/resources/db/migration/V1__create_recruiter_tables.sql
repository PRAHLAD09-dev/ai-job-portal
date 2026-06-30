-- V1__create_recruiter_tables.sql
-- Initial schema for the Recruiter Service: companies, recruiters,
-- company_locations, company_social_links. Owns ONLY company/recruiter
-- data, per PROJECT_SPECIFICATION.md Section 18 (Module Boundaries). The
-- recruiters.user_id column references the Auth Service's User.id by
-- VALUE ONLY — no foreign key constraint, since cross-service direct
-- database access/FKs are forbidden per DECISIONS.md.

CREATE TABLE companies (
    id                              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                            VARCHAR(200) NOT NULL,
    slug                            VARCHAR(220) NOT NULL,
    description                     TEXT,
    industry                        VARCHAR(50) NOT NULL,
    company_size                    VARCHAR(30) NOT NULL,
    founded_year                    INTEGER,
    website_url                     VARCHAR(500),
    email                           VARCHAR(255),
    phone_number                    VARCHAR(20),
    logo_url                        VARCHAR(1000),
    logo_cloudinary_public_id       VARCHAR(500),
    banner_url                      VARCHAR(1000),
    banner_cloudinary_public_id     VARCHAR(500),
    verification_status             VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    active_job_count                INTEGER NOT NULL DEFAULT 0,
    total_hires                     INTEGER NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_companies_slug UNIQUE (slug)
);

CREATE INDEX idx_companies_slug ON companies (slug);

CREATE TABLE recruiters (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL,
    email               VARCHAR(255) NOT NULL,
    full_name           VARCHAR(200) NOT NULL,
    phone_number        VARCHAR(20),
    title               VARCHAR(50) NOT NULL,
    designation         VARCHAR(150),
    profile_picture_url VARCHAR(1000),
    is_owner            BOOLEAN NOT NULL DEFAULT FALSE,
    company_id          UUID NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_recruiters_user_id UNIQUE (user_id),
    CONSTRAINT fk_recruiters_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE
);

CREATE INDEX idx_recruiters_user_id ON recruiters (user_id);
CREATE INDEX idx_recruiters_company_id ON recruiters (company_id);

CREATE TABLE company_locations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id          UUID NOT NULL,
    address_line        VARCHAR(255) NOT NULL,
    city                VARCHAR(100) NOT NULL,
    state               VARCHAR(100),
    country             VARCHAR(100) NOT NULL,
    postal_code         VARCHAR(20),
    is_headquarters     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_company_locations_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE
);

CREATE INDEX idx_company_locations_company_id ON company_locations (company_id);

CREATE TABLE company_social_links (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id          UUID NOT NULL,
    platform            VARCHAR(30) NOT NULL,
    url                 VARCHAR(500) NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_company_social_links_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE,
    CONSTRAINT uk_social_links_company_platform UNIQUE (company_id, platform)
);

CREATE INDEX idx_company_social_links_company_id ON company_social_links (company_id);

-- V1__create_auth_tables.sql
-- Initial schema for the Auth Service: users, roles, user_roles,
-- refresh_tokens, email_verification_tokens, password_reset_tokens.
-- Auth Service owns ONLY authentication/authorization data, per
-- PROJECT_SPECIFICATION.md Section 18 (Module Boundaries).

CREATE TABLE roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(30) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE users (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                   VARCHAR(255) NOT NULL,
    password_hash           VARCHAR(255) NOT NULL,
    first_name              VARCHAR(100) NOT NULL,
    last_name               VARCHAR(100) NOT NULL,
    status                  VARCHAR(30) NOT NULL,
    email_verified          BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts   INTEGER NOT NULL DEFAULT 0,
    account_locked          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);

CREATE TABLE user_roles (
    user_id     UUID NOT NULL,
    role_id     UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token_hash      VARCHAR(64) NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    device_info     VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);

CREATE TABLE email_verification_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token_hash      VARCHAR(64) NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at         TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_email_verification_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_email_verification_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_email_verification_tokens_token_hash ON email_verification_tokens (token_hash);

CREATE TABLE password_reset_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token_hash      VARCHAR(64) NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at         TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_password_reset_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_tokens_token_hash ON password_reset_tokens (token_hash);

-- V1__create_audit_logs_table.sql
-- Admin Service owns exactly one table: audit_logs. Every other read is
-- performed live, via OpenFeign, against Auth/Recruiter/Job/Application/
-- AI/Notification Service — this service has no other persisted state.

CREATE TABLE audit_logs (
    id            UUID PRIMARY KEY,
    admin_id      UUID NOT NULL,
    admin_email   VARCHAR(255) NOT NULL,
    action_type   VARCHAR(30) NOT NULL,
    target_type   VARCHAR(20),
    target_id     UUID,
    description   VARCHAR(500) NOT NULL,
    ip_address    VARCHAR(64),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_action_type ON audit_logs (action_type);
CREATE INDEX idx_audit_logs_admin_id ON audit_logs (admin_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at DESC);

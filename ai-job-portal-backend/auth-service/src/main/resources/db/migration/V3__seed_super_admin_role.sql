-- V3__seed_super_admin_role.sql
-- Adds the SUPER_ADMIN role, required by Admin Service (DAY09_ADMIN_SERVICE.md)
-- for its two-tier admin authorization (SUPER_ADMIN, ADMIN). Purely additive:
-- does not modify or remove any existing role or user data.

INSERT INTO roles (id, name, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'SUPER_ADMIN', now(), now());

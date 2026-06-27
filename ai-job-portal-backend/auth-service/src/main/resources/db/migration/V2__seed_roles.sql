-- V2__seed_roles.sql
-- Seeds the fixed set of platform roles. Roles are reference data managed
-- by migrations, not created/deleted via application APIs in this phase.

INSERT INTO roles (id, name, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'CANDIDATE', now(), now()),
    (gen_random_uuid(), 'RECRUITER', now(), now()),
    (gen_random_uuid(), 'ADMIN', now(), now());

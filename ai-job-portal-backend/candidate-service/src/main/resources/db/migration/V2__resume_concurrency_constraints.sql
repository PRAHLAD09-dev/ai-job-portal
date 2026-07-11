-- V2__resume_concurrency_constraints.sql
-- Closes a concurrency gap in resume upload/versioning: previously,
-- "exactly one ACTIVE resume per candidate" and "monotonically
-- increasing version number" were enforced only in application code
-- (read-then-write, no locking), so two near-simultaneous upload
-- requests for the same candidate could both succeed, leaving two
-- ACTIVE resumes and/or two resumes with the same version number.

-- At most one ACTIVE resume per candidate. A partial unique index only
-- constrains rows matching the WHERE clause, so ARCHIVED rows (of which
-- there can be many per candidate) are unaffected.
CREATE UNIQUE INDEX uk_resumes_one_active_per_candidate
    ON resumes (candidate_id)
    WHERE status = 'ACTIVE';

-- No two resumes for the same candidate may share a version number.
ALTER TABLE resumes
    ADD CONSTRAINT uk_resumes_candidate_version UNIQUE (candidate_id, version_number);

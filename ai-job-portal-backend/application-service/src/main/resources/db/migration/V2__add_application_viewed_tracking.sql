-- DAY11 - Viewed by Recruiter: tracks whether/when/by-whom a recruiter
-- first opened an application's detail view.

ALTER TABLE applications
    ADD COLUMN viewed    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN viewed_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN viewed_by UUID;

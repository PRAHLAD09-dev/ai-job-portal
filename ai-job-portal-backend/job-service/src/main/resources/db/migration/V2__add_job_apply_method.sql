-- DAY11 - Apply Methods: recruiter chooses how candidates apply
-- (EASY_APPLY in-app / QUICK_APPLY in-app with resume auto-selection /
-- EXTERNAL_APPLY redirect). Existing jobs default to EASY_APPLY, the
-- previous (and only) behavior.

ALTER TABLE jobs
    ADD COLUMN apply_method       VARCHAR(20) NOT NULL DEFAULT 'EASY_APPLY',
    ADD COLUMN external_apply_url VARCHAR(1000);

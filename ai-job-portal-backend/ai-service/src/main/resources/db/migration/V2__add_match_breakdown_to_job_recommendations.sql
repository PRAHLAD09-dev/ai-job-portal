-- V2__add_match_breakdown_to_job_recommendations.sql
-- DAY10 (AI Enhancement & ATS Intelligence): explainable, multi-dimensional
-- AI Job Match %. Adds six persisted dimension scores backing the existing
-- overall match_score column. Existing rows (pre-DAY10) default every new
-- dimension to 0; they are naturally replaced on the candidate's next
-- recommendation regeneration (RecommendationServiceImpl deletes and
-- re-inserts a candidate's rows on every call), so this is a safe backfill.

ALTER TABLE job_recommendations
    ADD COLUMN skill_match      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN experience_match INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN education_match  INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN project_match    INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN salary_match     INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN location_match   INTEGER NOT NULL DEFAULT 0;

ALTER TABLE job_recommendations ALTER COLUMN skill_match DROP DEFAULT;
ALTER TABLE job_recommendations ALTER COLUMN experience_match DROP DEFAULT;
ALTER TABLE job_recommendations ALTER COLUMN education_match DROP DEFAULT;
ALTER TABLE job_recommendations ALTER COLUMN project_match DROP DEFAULT;
ALTER TABLE job_recommendations ALTER COLUMN salary_match DROP DEFAULT;
ALTER TABLE job_recommendations ALTER COLUMN location_match DROP DEFAULT;

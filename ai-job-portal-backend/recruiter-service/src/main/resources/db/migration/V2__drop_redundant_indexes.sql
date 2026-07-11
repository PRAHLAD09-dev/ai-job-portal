-- V2__drop_redundant_indexes.sql
-- uk_companies_slug UNIQUE(slug) and uk_recruiters_user_id UNIQUE(user_id)
-- (both from V1) already create an implicit unique B-tree index in
-- Postgres. The explicit indexes below duplicated them exactly - pure
-- wasted storage and slightly slower writes, zero query benefit.

DROP INDEX IF EXISTS idx_companies_slug;
DROP INDEX IF EXISTS idx_recruiters_user_id;

-- V4__add_refresh_token_version.sql
-- Adds an optimistic-locking version column to refresh_tokens, backing
-- the @Version field on the RefreshToken entity. Closes a race condition
-- in refresh-token rotation where two concurrent requests replaying the
-- same token could both read it as usable before either committed its
-- revocation, letting both rotate successfully.
--
-- Existing rows default to 0, matching the entity's @Builder.Default.

ALTER TABLE refresh_tokens
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

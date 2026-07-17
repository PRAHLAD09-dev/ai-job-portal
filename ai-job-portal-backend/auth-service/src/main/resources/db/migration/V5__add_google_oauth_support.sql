-- DAY12 "Google OAuth": lets a User authenticate via Google instead of
-- (or in addition to) a local password. password_hash becomes nullable
-- since a Google-only account never sets one; auth_provider records how
-- the account was created (still LOCAL by default, so every existing row
-- is unaffected); google_id is the stable Google account identifier
-- ("sub" claim) used to look the account back up on subsequent logins.
ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE users
    ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL';

ALTER TABLE users
    ADD COLUMN google_id VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uk_users_google_id UNIQUE (google_id);

CREATE INDEX idx_users_google_id ON users (google_id) WHERE google_id IS NOT NULL;

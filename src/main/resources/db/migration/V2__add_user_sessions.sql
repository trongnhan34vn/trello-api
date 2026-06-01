-- ======================
-- UserSession
-- ======================
CREATE TABLE user_sessions
(
    id            UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID             NOT NULL REFERENCES "users" (id) ON DELETE CASCADE,
    cognito_id    VARCHAR(255)     NOT NULL,
    refresh_token TEXT             NOT NULL UNIQUE,
    expired_at    TIMESTAMP        NOT NULL,
    created_at    TIMESTAMP        NOT NULL DEFAULT NOW()
);


-- user
CREATE TABLE IF NOT EXISTS users
(
    -- id
    id                  BIGSERIAL PRIMARY KEY,

    -- fields
    name                VARCHAR(64) NOT NULL
);

-- user
CREATE TABLE IF NOT EXISTS users
(
    -- id
    id                  BIGSERIAL PRIMARY KEY,

    -- fields
    name                VARCHAR(64) NOT NULL
);

-- post
CREATE TABLE IF NOT EXISTS post
(
    -- id
    id                  BIGSERIAL PRIMARY KEY,

    -- fields
    title               VARCHAR(512) NOT NULL,
    content             TEXT NOT NULL,

    -- relationship
    user_id             BIGINT NOT NULL
);
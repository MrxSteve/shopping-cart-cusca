CREATE TABLE IF NOT EXISTS auth.users (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS auth.roles (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth.user_roles (
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

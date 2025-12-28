CREATE TABLE IF NOT EXISTS users (
    uuid UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
    uuid UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_roles (
    users_uuid UUID NOT NULL,
    roles_uuid UUID NOT NULL,
    PRIMARY KEY (users_uuid, roles_uuid),

    CONSTRAINT fk_users_roles_user
        FOREIGN KEY (users_uuid) REFERENCES users(uuid)
        ON DELETE CASCADE,

    CONSTRAINT fk_users_roles_role
        FOREIGN KEY (roles_uuid) REFERENCES roles(uuid)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id UUID PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    expiry_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE
);
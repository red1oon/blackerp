CREATE TABLE ad_user (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    client_id UUID NOT NULL,
    organization_id UUID,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE ad_user_role (
    user_id UUID NOT NULL REFERENCES ad_user(id),
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_username ON ad_user(username);
CREATE INDEX idx_user_client ON ad_user(client_id);
CREATE INDEX idx_user_org ON ad_user(organization_id);

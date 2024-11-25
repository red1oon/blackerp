-- File: src/test/resources/db/schema.sql
CREATE TABLE IF NOT EXISTS ad_table (
    id UUID PRIMARY KEY,
    name VARCHAR(60) NOT NULL CONSTRAINT ad_table_name_check CHECK (name ~ '^[a-z][a-z0-9_]*$'),
    display_name VARCHAR(60) NOT NULL,
    description VARCHAR(255),
    access_level VARCHAR(30) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(60) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(60) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ad_table_name_unique UNIQUE (name)
);
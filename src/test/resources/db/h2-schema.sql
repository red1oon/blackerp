-- File: src/test/resources/db/h2-schema.sql
-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS ad_table;

-- Create table with basic structure and H2-compatible syntax
CREATE TABLE ad_table (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    display_name VARCHAR(60) NOT NULL,
    description VARCHAR(255),
    access_level VARCHAR(30) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(60) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(60) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Add unique constraint
ALTER TABLE ad_table ADD CONSTRAINT uk_ad_table_name UNIQUE (name);

-- Add check constraint using H2 syntax
ALTER TABLE ad_table ADD CONSTRAINT chk_ad_table_name 
    CHECK (REGEXP_LIKE(name, '^[a-z][a-z0-9_]*$'));
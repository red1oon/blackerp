CREATE TABLE ad_table (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    access_level VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT name_format CHECK (name ~ '^[a-z][a-z0-9_]*$')
);

CREATE INDEX idx_ad_table_name ON ad_table(name);
CREATE INDEX idx_ad_table_active ON ad_table(active);

COMMENT ON TABLE ad_table IS 'Application Dictionary Table Definitions';
COMMENT ON COLUMN ad_table.id IS 'Time-based UUID primary key';
COMMENT ON COLUMN ad_table.name IS 'Technical name (lowercase with underscores)';
COMMENT ON COLUMN ad_table.display_name IS 'Human readable name';
COMMENT ON COLUMN ad_table.description IS 'Optional description';
COMMENT ON COLUMN ad_table.access_level IS 'Access level (SYSTEM, CLIENT, ORGANIZATION, etc)';
COMMENT ON COLUMN ad_table.version IS 'Optimistic locking version';

-- File: src/main/resources/db/migration/V2__Create_relationship_schema.sql
CREATE TABLE ad_table_relationship (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    source_table VARCHAR(100) NOT NULL,
    target_table VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    source_column VARCHAR(100) NOT NULL,
    target_column VARCHAR(100) NOT NULL,
    delete_rule VARCHAR(20) NOT NULL,
    update_rule VARCHAR(20) NOT NULL,
    junction_table VARCHAR(100),
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uk_relationship_name UNIQUE (name),
    CONSTRAINT fk_source_table FOREIGN KEY (source_table) REFERENCES ad_table(name),
    CONSTRAINT fk_target_table FOREIGN KEY (target_table) REFERENCES ad_table(name),
    CONSTRAINT fk_junction_table FOREIGN KEY (junction_table) REFERENCES ad_table(name),
    CONSTRAINT chk_relationship_type CHECK (type IN ('ONE_TO_ONE', 'ONE_TO_MANY', 'MANY_TO_MANY')),
    CONSTRAINT chk_delete_rule CHECK (delete_rule IN ('RESTRICT', 'CASCADE', 'SET_NULL', 'NO_ACTION')),
    CONSTRAINT chk_update_rule CHECK (update_rule IN ('RESTRICT', 'CASCADE', 'SET_NULL', 'NO_ACTION'))
);

CREATE INDEX idx_relationship_source ON ad_table_relationship(source_table);
CREATE INDEX idx_relationship_target ON ad_table_relationship(target_table);
CREATE INDEX idx_relationship_active ON ad_table_relationship(active);

COMMENT ON TABLE ad_table_relationship IS 'Table Relationship Definitions';
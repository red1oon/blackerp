-- Drop existing tables if they exist
DROP TABLE IF EXISTS ad_tab_order_by;
DROP TABLE IF EXISTS ad_tab_display_column;
DROP TABLE IF EXISTS ad_tab_query_column;
DROP TABLE IF EXISTS ad_tab;
DROP TABLE IF EXISTS ad_table_relationship;
DROP TABLE IF EXISTS ad_table;

-- Create ad_table with H2-compatible syntax
CREATE TABLE ad_table (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    access_level VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uk_table_name UNIQUE (name)
);

-- Use REGEXP_LIKE for H2 name format check
ALTER TABLE ad_table ADD CONSTRAINT chk_table_name 
    CHECK (REGEXP_LIKE(name, '^[a-z][a-z0-9_]*$'));

-- Create relationship table
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

-- Create tab and related tables
CREATE TABLE ad_tab (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    table_name VARCHAR(100) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uk_tab_name UNIQUE (name),
    CONSTRAINT fk_tab_table FOREIGN KEY (table_name) REFERENCES ad_table(name)
);

CREATE TABLE ad_tab_query_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_query_tab FOREIGN KEY (tab_id) REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE ad_tab_display_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_display_tab FOREIGN KEY (tab_id) REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE ad_tab_order_by (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    direction VARCHAR(4) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_order_tab FOREIGN KEY (tab_id) REFERENCES ad_tab(id) ON DELETE CASCADE,
    CONSTRAINT chk_direction CHECK (direction IN ('ASC', 'DESC'))
);

-- Create indexes for better performance
CREATE INDEX idx_table_name ON ad_table(name);
CREATE INDEX idx_table_active ON ad_table(active);
CREATE INDEX idx_relationship_source ON ad_table_relationship(source_table);
CREATE INDEX idx_relationship_target ON ad_table_relationship(target_table);
CREATE INDEX idx_relationship_active ON ad_table_relationship(active);
CREATE INDEX idx_tab_name ON ad_tab(name);
CREATE INDEX idx_tab_table ON ad_tab(table_name);
-- src/test/resources/db/schema.sql
-- Add after existing tables

CREATE TABLE IF NOT EXISTS ad_tab (
    id UUID PRIMARY KEY,
    name VARCHAR(60) NOT NULL CONSTRAINT uk_tab_name UNIQUE,
    display_name VARCHAR(60) NOT NULL,
    description VARCHAR(255),
    table_name VARCHAR(60) NOT NULL,
    created TIMESTAMP NOT NULL,
    created_by VARCHAR(60) NOT NULL,
    updated TIMESTAMP NOT NULL,
    updated_by VARCHAR(60) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_tab_table FOREIGN KEY (table_name) 
        REFERENCES ad_table(name)
);

CREATE TABLE IF NOT EXISTS ad_tab_query_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(60) NOT NULL,
    sequence INT NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_query_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ad_tab_display_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(60) NOT NULL,
    sequence INT NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_display_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ad_tab_order_by (
    tab_id UUID NOT NULL,
    column_name VARCHAR(60) NOT NULL,
    direction VARCHAR(4) NOT NULL,
    sequence INT NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_order_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE,
    CONSTRAINT chk_direction CHECK (direction IN ('ASC', 'DESC'))
);
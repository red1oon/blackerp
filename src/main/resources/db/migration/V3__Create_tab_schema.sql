-- src/main/resources/db/migration/V3__Create_tab_schema.sql
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
    CONSTRAINT fk_tab_table FOREIGN KEY (table_name) 
        REFERENCES ad_table(name)
);

CREATE INDEX idx_tab_name ON ad_tab(name);
CREATE INDEX idx_tab_table ON ad_tab(table_name);

CREATE TABLE ad_tab_query_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_query_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE ad_tab_display_column (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_display_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE
);

CREATE TABLE ad_tab_order_by (
    tab_id UUID NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    direction VARCHAR(4) NOT NULL,
    sequence INTEGER NOT NULL,
    PRIMARY KEY (tab_id, column_name),
    CONSTRAINT fk_order_tab FOREIGN KEY (tab_id) 
        REFERENCES ad_tab(id) ON DELETE CASCADE,
    CONSTRAINT chk_direction CHECK (direction IN ('ASC', 'DESC'))
);
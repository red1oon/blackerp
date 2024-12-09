-- AD Rule table
CREATE TABLE ad_rule (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    rule_type VARCHAR(20) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    expression TEXT NOT NULL,
    error_message TEXT,
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL, 
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- AD Rule Parameters
CREATE TABLE ad_rule_parameter (
    rule_id UUID NOT NULL REFERENCES ad_rule(id),
    name VARCHAR(40) NOT NULL,
    data_type VARCHAR(20) NOT NULL,
    mandatory BOOLEAN DEFAULT false,
    default_value TEXT,
    PRIMARY KEY (rule_id, name)
);

-- AD Validation Rule table
CREATE TABLE ad_validation_rule (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    entity_type VARCHAR(40) NOT NULL,
    field_name VARCHAR(40) NOT NULL,
    expression TEXT NOT NULL,
    error_message TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- AD Status Line table
CREATE TABLE ad_status_line (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    document_type VARCHAR(40) NOT NULL,
    from_status VARCHAR(40) NOT NULL,
    to_status VARCHAR(40) NOT NULL,
    role_id UUID,
    sequence INT NOT NULL DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- AD Status Line Conditions
CREATE TABLE ad_status_line_condition (
    status_line_id UUID NOT NULL REFERENCES ad_status_line(id),
    rule_id UUID NOT NULL REFERENCES ad_rule(id),
    sequence INT NOT NULL DEFAULT 0,
    PRIMARY KEY (status_line_id, rule_id)
);

-- Indexes
CREATE INDEX idx_rule_type ON ad_rule(rule_type);
CREATE INDEX idx_rule_entity ON ad_rule(entity_type);
CREATE INDEX idx_validation_entity ON ad_validation_rule(entity_type);
CREATE INDEX idx_status_document ON ad_status_line(document_type);
CREATE INDEX idx_status_from ON ad_status_line(from_status);
CREATE INDEX idx_status_to ON ad_status_line(to_status);

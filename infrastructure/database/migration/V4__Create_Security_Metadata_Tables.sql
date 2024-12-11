-- File: infrastructure/database/migration/V4__Create_Security_Metadata_Tables.sql

-- Security Rule Definition
CREATE TABLE ad_security_rule (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(40) NOT NULL, -- TABLE, WINDOW, PROCESS etc
    entity_id UUID NOT NULL,
    role_id UUID REFERENCES ad_role(id),
    rule_type VARCHAR(20) NOT NULL, -- READ_PERMISSION, WRITE_PERMISSION etc
    expression TEXT, -- AD metadata expression
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- Role Permission Assignment
CREATE TABLE ad_role_permission (
    id UUID PRIMARY KEY,
    role_id UUID NOT NULL REFERENCES ad_role(id),
    permission_type VARCHAR(20) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id UUID NOT NULL,
    validation_rule TEXT, -- AD metadata validation rule
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- Security Rule Conditions
CREATE TABLE ad_security_rule_condition (
    rule_id UUID NOT NULL REFERENCES ad_security_rule(id),
    condition_type VARCHAR(20) NOT NULL,
    expression TEXT NOT NULL,
    sequence INT NOT NULL DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rule_id, sequence)
);

-- Security Audit Log
CREATE TABLE ad_security_audit (
    id UUID PRIMARY KEY,
    event_type VARCHAR(40) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES ad_user(id),
    role_id UUID REFERENCES ad_role(id),
    access_type VARCHAR(20) NOT NULL,
    result BOOLEAN NOT NULL,
    failure_reason TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Performance
CREATE INDEX idx_security_rule_entity ON ad_security_rule(entity_type, entity_id);
CREATE INDEX idx_security_rule_role ON ad_security_rule(role_id) WHERE role_id IS NOT NULL;
CREATE INDEX idx_role_permission_role ON ad_role_permission(role_id);
CREATE INDEX idx_role_permission_entity ON ad_role_permission(entity_type, entity_id);
CREATE INDEX idx_security_audit_entity ON ad_security_audit(entity_type, entity_id);
CREATE INDEX idx_security_audit_user ON ad_security_audit(user_id);
CREATE INDEX idx_security_audit_created ON ad_security_audit(created_at);

-- Comments
COMMENT ON TABLE ad_security_rule IS 'AD metadata-driven security rules';
COMMENT ON TABLE ad_role_permission IS 'Dynamic role-permission assignments';
COMMENT ON TABLE ad_security_rule_condition IS 'Conditional expressions for security rules';
COMMENT ON TABLE ad_security_audit IS 'Security access audit log';
-- Document Type Definition
CREATE TABLE ad_document_type (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    base_table_id UUID NOT NULL,
    lines_table_id UUID,
    workflow_id UUID,
    default_status VARCHAR(10) NOT NULL,
    is_sotrx BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- Document Status Definition
CREATE TABLE ad_doc_status (
    code VARCHAR(10) NOT NULL,
    type_id UUID NOT NULL REFERENCES ad_document_type(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT false,
    is_closing BOOLEAN DEFAULT false,
    sequence INT NOT NULL DEFAULT 10,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1,
    PRIMARY KEY (code, type_id)
);

-- Document Status Transitions
CREATE TABLE ad_doc_status_transition (
    id UUID PRIMARY KEY,
    type_id UUID NOT NULL REFERENCES ad_document_type(id),
    from_status VARCHAR(10) NOT NULL,
    to_status VARCHAR(10) NOT NULL,
    role_id UUID,
    condition_expr TEXT,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1,
    FOREIGN KEY (type_id, from_status) REFERENCES ad_doc_status(type_id, code),
    FOREIGN KEY (type_id, to_status) REFERENCES ad_doc_status(type_id, code)
);

-- Document Records
CREATE TABLE ad_document (
    id UUID PRIMARY KEY,
    type_id UUID NOT NULL REFERENCES ad_document_type(id),
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(10) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

-- Document Attributes (Key-Value Store)
CREATE TABLE ad_document_attribute (
    document_id UUID NOT NULL REFERENCES ad_document(id),
    name VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (document_id, name)
);

-- Document Change History
CREATE TABLE ad_document_history (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL REFERENCES ad_document(id),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by UUID NOT NULL,
    previous_status VARCHAR(10),
    new_status VARCHAR(10),
    changes JSONB NOT NULL,
    reason TEXT
);

-- Indexes for Performance
CREATE INDEX idx_doc_type_name ON ad_document_type(name);
CREATE INDEX idx_doc_status ON ad_document(status);
CREATE INDEX idx_doc_type ON ad_document(type_id);
CREATE INDEX idx_doc_history ON ad_document_history(document_id, changed_at);
CREATE INDEX idx_doc_attribute ON ad_document_attribute(document_id);

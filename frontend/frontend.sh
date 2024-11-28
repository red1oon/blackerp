#!/bin/bash

# Output file path
OUTPUT_FILE="src/main/resources/db/migration/V5__Seed_initial_data.sql"

# Ensure the directory exists before creating the file
mkdir -p src/main/resources/db/migration

# Write the SQL content to the output file
cat > "$OUTPUT_FILE" <<EOL
INSERT INTO ad_table (
    id, name, display_name, description, access_level,
    created, created_by, updated, updated_by, version, active
) VALUES (
    '550e8400-e29b-41d4-a716-446655440000', 
    'business_partner', 
    'Business Partner', 
    'Business partner master data', 
    'ORGANIZATION',
    CURRENT_TIMESTAMP, 
    'system', 
    CURRENT_TIMESTAMP, 
    'system',
    0,
    true
),
(
    '550e8400-e29b-41d4-a716-446655440001', 
    'product', 
    'Product', 
    'Product master data', 
    'ORGANIZATION',
    CURRENT_TIMESTAMP, 
    'system', 
    CURRENT_TIMESTAMP, 
    'system',
    0,
    true
);
EOL

echo "SQL script created at $OUTPUT_FILE"

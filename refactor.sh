#!/bin/bash

# Define colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Map of imports to fix
declare -A IMPORT_MAP=(
    ["import org.blackerp.shared.ValidationError"]="import org.blackerp.domain.core.shared.ValidationError"
    ["import org.blackerp.domain.table.ADTable"]="import org.blackerp.domain.core.ad.table.ADTable"
    ["import org.blackerp.domain.EntityMetadata"]="import org.blackerp.domain.core.EntityMetadata"
    ["import org.blackerp.domain.DomainEntity"]="import org.blackerp.domain.core.DomainEntity"
    ["import org.blackerp.validation.Validator"]="import org.blackerp.domain.core.validation.Validator"
    ["import org.blackerp.plugin.Plugin"]="import org.blackerp.domain.plugin.Plugin"
    ["import org.blackerp.domain.values.DataType"]="import org.blackerp.domain.core.values.DataType"
)

# Counters
total_files=0
files_fixed=0
files_ignored=0

# Function to fix imports in a file
fix_imports() {
    local file="$1"
    echo -e "\n${YELLOW}Checking imports in: $file${NC}"
    local changes=0

    for old_import in "${!IMPORT_MAP[@]}"; do
        # Match only import statements using regex
        if grep -E "^$old_import" "$file" > /dev/null; then
            echo -e "${GREEN}Found and fixing: $old_import -> ${IMPORT_MAP[$old_import]}${NC}"
            # Replace the import using sed
            sed -i "s|$old_import|${IMPORT_MAP[$old_import]}|g" "$file"
            ((changes++))
        fi
    done

    if ((changes > 0)); then
        echo -e "${GREEN}Fixed $changes imports in $file${NC}"
        ((files_fixed++))
    else
        ((files_ignored++))
    fi
}

# Find all Kotlin files in the project
total_files=$(find . -name "*.kt" -type f ! -path "*/build/*" ! -path "*/.gradle/*" | wc -l)
echo -e "${BLUE}Found $total_files Kotlin files${NC}"

# Process each Kotlin file
find . -name "*.kt" -type f ! -path "*/build/*" ! -path "*/.gradle/*" | while read -r file; do
    fix_imports "$file"
done

# Summary
echo -e "\n${BLUE}Summary:${NC}"
echo -e "${GREEN}Total files checked: $total_files${NC}"
echo -e "${GREEN}Files fixed: $files_fixed${NC}"
echo -e "${YELLOW}Files ignored: $files_ignored${NC}"

# Debugging Message if No Fixes
if ((files_fixed == 0)); then
    echo -e "${RED}No fixes applied. Please ensure the IMPORT_MAP keys match exactly the errors in your files.${NC}"
fi

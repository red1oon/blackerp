#!/bin/bash

# Define colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Output files
CORRECT_IMPORTS_FILE="correct_importlist.txt"
MISSING_IMPORTS_FILE="missing_imports.txt"

# Clean up previous outputs
> "$CORRECT_IMPORTS_FILE"
> "$MISSING_IMPORTS_FILE"

echo -e "${BLUE}Scanning Kotlin files to generate correct imports...${NC}"

# Step 1: Build correct import list
find . -name "*.kt" -type f ! -path "*/build/*" ! -path "*/.gradle/*" | while read -r file; do
    # Extract the package declaration
    package=$(grep -m 1 "^package " "$file" | awk '{print $2}')
    if [[ -n "$package" ]]; then
        # Get the class name (file name without extension)
        class_name=$(basename "$file" .kt)
        # Combine package and class name
        echo "$package.$class_name" >> "$CORRECT_IMPORTS_FILE"
    fi
done

# Sort and remove duplicates from the list
sort -u "$CORRECT_IMPORTS_FILE" -o "$CORRECT_IMPORTS_FILE"

echo -e "${GREEN}Correct import list generated: $CORRECT_IMPORTS_FILE${NC}"

# Step 2: Validate imports in each Kotlin file
echo -e "${BLUE}Validating imports in Kotlin files...${NC}"

find . -name "*.kt" -type f ! -path "*/build/*" ! -path "*/.gradle/*" | while read -r file; do
    echo -e "${YELLOW}Checking: $file${NC}"
    # Extract all import statements
    grep "^import " "$file" | awk '{print $2}' | while read -r import; do
        # Check if the import exists in the correct import list
        if ! grep -q "$import" "$CORRECT_IMPORTS_FILE"; then
            echo -e "${RED}Missing or incorrect import: $import in $file${NC}"
            echo "$file: $import" >> "$MISSING_IMPORTS_FILE"
        fi
    done
done

echo -e "${GREEN}Validation complete.${NC}"
echo -e "${BLUE}Correct imports listed in: $CORRECT_IMPORTS_FILE${NC}"
echo -e "${RED}Missing or incorrect imports listed in: $MISSING_IMPORTS_FILE${NC}"

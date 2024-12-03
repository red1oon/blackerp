#!/bin/bash

# Define colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Input files
BUILD_ERRORS_FILE="build_errors.txt"
CORRECT_IMPORTS_FILE="correct_importlist.txt"

# Ensure the required files exist
if [[ ! -f "$BUILD_ERRORS_FILE" ]]; then
    echo -e "${RED}Error: $BUILD_ERRORS_FILE not found. Please provide the build output.${NC}"
    exit 1
fi

if [[ ! -f "$CORRECT_IMPORTS_FILE" ]]; then
    echo -e "${RED}Error: $CORRECT_IMPORTS_FILE not found. Please generate it using the appropriate script.${NC}"
    exit 1
fi

# Step 1: Extract unresolved references from the build errors
echo -e "${BLUE}Parsing unresolved references from build_errors.txt...${NC}"
grep "Unresolved reference:" "$BUILD_ERRORS_FILE" | awk -F ':' '{print $2 ":" $3}' | awk '{print $1 ":" $2}' | sort | uniq > unresolved_references.txt
echo -e "${GREEN}Unresolved references extracted to unresolved_references.txt${NC}"

# Step 2: Resolve and fix imports
echo -e "${BLUE}Resolving and fixing imports...${NC}"

while read -r line; do
    # Extract the file and unresolved reference
    file=$(echo "$line" | awk -F':' '{print $1}')
    unresolved=$(echo "$line" | awk -F':' '{print $2}' | xargs)

    # Look up the correct import in the correct imports list
    correct_import=$(grep -F ".$unresolved" "$CORRECT_IMPORTS_FILE" | awk -F':' '{print $2}')

    if [[ -n "$correct_import" ]]; then
        echo -e "${YELLOW}Fixing: $unresolved in $file${NC}"

        # Check if the file already has the correct import
        if grep -q "$correct_import" "$file"; then
            echo -e "${BLUE}Import already present: $correct_import in $file${NC}"
        else
            # Check if there is an existing import block
            if grep -q "^import " "$file"; then
                echo -e "${GREEN}Inserting missing import: $correct_import${NC}"
                sed -i "/^import /a import $correct_import" "$file"
            else
                echo -e "${GREEN}Adding new import block with: $correct_import${NC}"
                sed -i "/^package /a import $correct_import" "$file"
            fi
        fi
    else
        echo -e "${RED}Could not resolve: $unresolved in $file. Manual intervention required.${NC}"
    fi
done < unresolved_references.txt

echo -e "${GREEN}Import fixing complete.${NC}"

#!/bin/bash
# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# Base package name
BASE_PACKAGE="org.blackerp"

# Function to check and fix package declarations
check_package() {
    local file="$1"

    # Skip build and .gradle directories
    if [[ "$file" =~ build/ || "$file" =~ .gradle/ ]]; then
        return
    fi

    echo -e "\n${YELLOW}Checking: $file${NC}"

    # Extract the relative path and calculate the expected package
    local rel_path="${file#*/}"  # Remove leading directories
    local module_path=$(dirname "$rel_path")  # Get the directory path
    local package_path="${module_path//\//.}"  # Replace / with . for package format
    local expected_package="$BASE_PACKAGE.$package_path"

    echo -e "${BLUE}Expected package: $expected_package${NC}"

    # Extract the current package from the file
    local current_package
    current_package=$(grep "^package " "$file" | awk '{print $2}')
    
    # Compare and replace if different
    if [[ "$current_package" != "$expected_package" ]]; then
        echo -e "${RED}Incorrect package: $current_package${NC}"
        sed -i "s|^package .*|package $expected_package|" "$file"
        echo -e "${GREEN}Updated package to: $expected_package${NC}"
    else
        echo -e "${GREEN}Package is correct.${NC}"
    fi
}

# Count and process files
count=0
total=$(find . -name "*.kt" -not -path "*/build/*" -not -path "*/.gradle/*" | wc -l)
echo -e "${BLUE}Found $total Kotlin files${NC}"

find . -name "*.kt" -not -path "*/build/*" -not -path "*/.gradle/*" | while read -r file; do
    ((count++))
    echo -e "${BLUE}File $count of $total${NC}"
    check_package "$file"
done

echo -e "\n${GREEN}Complete - processed $count files${NC}"

#!/bin/bash
# check_remaining_issues.sh

# Define colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "Checking remaining issues..."

# Run Kotlin compilation and capture errors
./gradlew compileKotlin > compile_output.txt 2>&1

# Output file for uncategorized errors
UNCATEGORIZED_ERRORS_FILE="uncategorized_errors.txt"

# Clean up previous output for uncategorized errors
> "$UNCATEGORIZED_ERRORS_FILE"

# Extract and analyze different types of errors
echo "Unresolved References:"
grep "Unresolved reference:" compile_output.txt

echo -e "\nMissing Implementations:"
grep "does not implement" compile_output.txt

echo -e "\nRedeclarations:"
grep "Redeclaration:" compile_output.txt

echo -e "\nType Mismatches:"
grep "Type mismatch:" compile_output.txt

# Capture uncategorized errors
echo -e "\nOther Kotlin Compilation Errors:"
KNOWN_ERROR_PATTERNS="Unresolved reference:|does not implement|Redeclaration:|Type mismatch:"
grep -E "e: .*" compile_output.txt | grep -v -E "$KNOWN_ERROR_PATTERNS" > "$UNCATEGORIZED_ERRORS_FILE"

# Output uncategorized errors to console
if [[ -s "$UNCATEGORIZED_ERRORS_FILE" ]]; then
    cat "$UNCATEGORIZED_ERRORS_FILE"
fi

# Final status
if grep -q "e: " compile_output.txt; then
    echo -e "\n${RED}BUILD STATUS: FAIL${NC}"
else
    echo -e "\n${BLUE}BUILD STATUS: SUCCESS${NC}"
fi

./generate_project_listing.sh
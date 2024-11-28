#!/bin/bash

# Output file to store the results
OUTPUT_FILE="frontend_listing.txt"
> "$OUTPUT_FILE"  # Empty the file if it already exists

# Step 1: Generate a directory structure overview excluding clutter folders
echo "Generating folder structure..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"
tree -a -I "node_modules|target|build|out|.git|.idea|*.class|*.o|*.log|*.iml|*.swp|*~|dist|coverage|public|*.d.ts|test|__tests__|mocks|stories|*.map|*.css|*.scss|*.config.*|.*" >> "$OUTPUT_FILE"
echo -e "\n" >> "$OUTPUT_FILE"

# Step 2: Add file paths and cleaned content, excluding irrelevant files
echo "Generating file details..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"

# Traverse the directory and process relevant files, excluding irrelevant folders and files
find . -type f \( -name "*.tsx" -o -name "*.ts" -o -name "*.json" -o -name "*.cjs" \) \
    ! -path "*/dist/*" ! -path "*/coverage/*" ! -path "*/public/*" ! -path "*/test/*" ! -path "*/__tests__/*" \
    ! -path "*/mocks/*" ! -path "*/stories/*" ! -path "*/node_modules/*" ! -path "./.*" \
    ! -name "*.d.ts" ! -name "*.map" ! -name "*.config.*" ! -name "*.scss" ! -name "*.css" | while read -r file; do

    # Skip JSON files except for `package.json`
    if [[ $file == *.json ]] && [[ $file != *"package.json" ]]; then
        continue
    fi

    # Write the file path to the output file with a single separator line (no newline after separator)
    echo -e "\nFile: $file" >> "$OUTPUT_FILE"
    echo "-------------------------------------------" >> "$OUTPUT_FILE"

    # Remove blank lines, carriage returns, comments, and reduce excessive spaces
    sed '/^\s*$/d' "$file" | tr -d '\r' \
    | sed -e 's/^[ \t]*//;s/[ \t]*$//' \
          -e '/\/\//d' \
          -e '/\/\*/,/\*\//d' \
          -e 's/: /:/g' \
          -e 's/ = /=/g' \
          -e 's/, /,/g' \
          -e 's/( /(/g' \
          -e 's/ )/)/g' \
          -e 's/{[ \t]*/{/g' \
          -e 's/[ \t]*}/}/g' \
          -e 's/ -> /->/g' \
          -e 's/; /;/g' \
          -e 's/< /</g' \
          -e 's/ >/>/g' \
          -e 's/^[ \t]*@/@/g' \
          -e 's/ new /new /g' >> "$OUTPUT_FILE"

    # Add a newline to separate content between files, but only at the start of the next file
done

# Remove multiple spaces introduced during content concatenation, leaving only a single space where needed
sed -i 's/  */ /g' "$OUTPUT_FILE"

echo "Compact and filtered project listing saved to $OUTPUT_FILE"

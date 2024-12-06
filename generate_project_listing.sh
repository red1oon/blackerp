#!/bin/bash

# Output file to store the results
OUTPUT_FILE="KotlinCoreApp.txt"
> "$OUTPUT_FILE"  # Empty the file if it already exists

# Step 1: Generate a directory structure overview excluding build, frontend, and bin
echo "Generating folder structure..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"
tree -a -I "node_modules|target|build|out|.git|.idea|*.class|*.o|*.log|*.iml|*.swp|*~|build|frontend|bin" >> "$OUTPUT_FILE"
echo -e "\n" >> "$OUTPUT_FILE"

# Step 2: Add file paths and cleaned content, excluding build, frontend, and bin folders
echo "Generating file details..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"

# Traverse the directory and process relevant files, excluding build, frontend, and bin
find . -type f \( -name "*.yml" -o -name "*.sql" -o -name "*.kt" -o -name "*.kts" -o -name "*.java" \) \
    ! -path "*/build/*" ! -path "*/frontend/*" ! -path "*/bin/*" | while read -r file; do
    # Write the file path to the output file with a single separator line (no newline after separator)
    echo -e "\nFile: $file" >> "$OUTPUT_FILE"
    echo "-------------------------------------------" >> "$OUTPUT_FILE"

    # Remove blank lines, carriage returns, and reduce excessive spaces
    sed '/^\s*$/d' "$file" | tr -d '\r' | sed 's/^[ \t]*//;s/[ \t]*$//' | tr '\n' ' ' \
    | sed -e 's/: /:/g' \
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

echo "Compact and optimized project listing saved to $OUTPUT_FILE"

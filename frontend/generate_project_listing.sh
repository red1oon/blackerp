#!/bin/bash

# Output file to store the results
OUTPUT_FILE="project_listing.txt"
> "$OUTPUT_FILE"  # Empty the file if it already exists

# Step 1: Generate a directory tree, excluding clutter
echo "Generating folder structure..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"
# Exclude folders/files that commonly clutter the tree view
tree -a -I "node_modules|target|build|out|.git|.idea|*.class|*.o|*.log|*.iml|*.swp|*~" >> "$OUTPUT_FILE"
echo -e "\n\n" >> "$OUTPUT_FILE"

# Step 2: Add file paths and content
echo "Generating file details..." >> "$OUTPUT_FILE"
echo "-------------------------------------------" >> "$OUTPUT_FILE"

# Function to extract classpath based on file path
get_classpath() {
    local file_path="$1"
    # Replace slashes, remove extension, and replace directory keywords
    local classpath=$(echo "$file_path" | sed -e 's#^\./##' -e 's#/#.#g' -e 's#\.kt$##' -e 's#\.kts$##' -e 's#\.yml$##' -e 's#\.sql$##')
    echo "$classpath"
}

# Traverse the directory and process relevant files
find . -type f \( -name "*.tsx" -o -name "*.ts" -o -name "*.json" -o -name "*.cjs" \) | while read -r file; do
    # Get the classpath representation for the file
    classpath=$(get_classpath "$file")

    # Write classpath and file contents to the output file
    echo "File: $file" >> "$OUTPUT_FILE"
    echo "// Classpath: $classpath" >> "$OUTPUT_FILE"
    echo >> "$OUTPUT_FILE"
    cat "$file" >> "$OUTPUT_FILE"
    echo -e "\n\n" >> "$OUTPUT_FILE"
done

echo "Documentation output saved to $OUTPUT_FILE"

#!/bin/bash

# Define paths for BlackERP and NewERP
BLACKERP_DIR="../blackerp"
NEWERP_DIR="./NewERP"
SOURCE_PATH="$BLACKERP_DIR/application/src/main/kotlin/org/blackerp/api"
TARGET_PATH="$NEWERP_DIR/application/api"

# Check if source exists
if [ ! -d "$SOURCE_PATH" ]; then
  echo "Error: Source path $SOURCE_PATH does not exist."
  exit 1
fi

# Create target directory in NewERP
echo "Creating target directory: $TARGET_PATH"
mkdir -p "$TARGET_PATH"

# Copy files from BlackERP to NewERP
echo "Migrating validation module to domain/validation..."
cp -r "$SOURCE_PATH/"* "$TARGET_PATH/"
echo "Validation module migration completed."

# Migrate tests
SOURCE_TEST_PATH="$BLACKERP_DIR/validation/src/test/kotlin/org/blackerp/validation"
TARGET_TEST_PATH="$NEWERP_DIR/domain/validation"

if [ -d "$SOURCE_TEST_PATH" ]; then
  echo "Migrating tests for validation module..."
  mkdir -p "$TARGET_TEST_PATH"
  cp -r "$SOURCE_TEST_PATH/"* "$TARGET_TEST_PATH/"
  echo "Tests for validation module migrated successfully."
else
  echo "No tests found for validation module."
fi

# Verify migration
echo "Verifying migration for validation module..."
tree -L 3 "$TARGET_PATH"
tree -L 3 "$TARGET_TEST_PATH"

echo "Migration of validation module completed. Next steps:"
echo "1. Review the code in $TARGET_PATH."
echo "2. Run './gradlew build' to verify the build."
echo "3. Run './gradlew test' to verify the tests."

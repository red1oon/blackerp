#!/bin/bash

# Exit on error
set -e

echo "Verifying BlackERP project setup..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed"
    exit 1
fi

# Verify project structure
declare -a dirs=(
    "src/main/kotlin/org/blackerp/domain"
    "src/main/kotlin/org/blackerp/repository"
    "src/main/kotlin/org/blackerp/service"
    "src/main/kotlin/org/blackerp/web"
    "src/test/kotlin/org/blackerp/domain"
    "src/test/kotlin/org/blackerp/repository"
    "src/test/kotlin/org/blackerp/service"
    "src/test/kotlin/org/blackerp/web"
)

for dir in "${dirs[@]}"; do
    if [ ! -d "$dir" ]; then
        echo "Error: Directory $dir is missing"
        exit 1
    fi
done

# Verify configuration files
declare -a files=(
    "docker-compose.yml"
    "src/main/resources/application.yml"
    ".vscode/settings.json"
    ".gitignore"
    "README.md"
    "LICENSE"
)

for file in "${files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "Error: File $file is missing"
        exit 1
    fi
done

# Try to build the project
./gradlew clean build

echo "Verification completed successfully!"

#!/bin/bash

# Function to check and add plugins
ensure_plugin() {
    local file=$1
    local plugin=$2
    if ! grep -q "$plugin" "$file"; then
        echo "Adding plugin: $plugin"
        sed -i "/plugins {/a\\    $plugin" "$file"
    else
        echo "Plugin $plugin already exists."
    fi
}

# Function to add dependencies in Kotlin DSL format
add_dependency_kts() {
    local file=$1
    local dependency=$2

    # Check if dependency already exists
    if ! grep -q "$dependency" "$file"; then
        echo "Adding dependency: $dependency"

        # Insert dependency inside the `dependencies` block
        if grep -q "dependencies {" "$file"; then
            sed -i "/dependencies {/a\\    $dependency" "$file"
        else
            # Add `dependencies {}` block if missing
            echo -e "\ndependencies {\n    $dependency\n}" >> "$file"
        fi
    else
        echo "Dependency $dependency already exists."
    fi
}

echo "Starting build fix script..."

# Step 1: Check Gradle installation
if ! command -v gradle &> /dev/null; then
    echo "Gradle is not installed. Install Gradle and try again."
    exit 1
fi

echo "Gradle is installed."
gradle -v

# Step 2: Check for build.gradle.kts
if [ ! -f "build.gradle.kts" ]; then
    echo "Error: build.gradle.kts file not found."
    exit 1
fi

echo "Dependency file found: build.gradle.kts"

# Step 3: Ensure necessary plugins are added (but avoid duplicates)
ensure_plugin "build.gradle.kts" 'kotlin("jvm") version "1.8.0"'
ensure_plugin "build.gradle.kts" 'application'

# Step 4: Add dependencies
add_dependency_kts "build.gradle.kts" 'implementation("io.arrow-kt:arrow-core:1.2.0")'
add_dependency_kts "build.gradle.kts" 'implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")'

# Step 5: Sync dependencies
echo "Syncing Gradle dependencies..."
./gradlew dependencies --refresh-dependencies

if [ $? -ne 0 ]; then
    echo "Error syncing dependencies."
    exit 1
fi

# Step 6: Clean and rebuild
echo "Cleaning and rebuilding the project..."
./gradlew clean build

if [ $? -ne 0 ]; then
    echo "Build failed. Check the errors above."
    exit 1
fi

echo "Build script completed successfully."

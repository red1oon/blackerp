#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print section header
print_header() {
    echo -e "\n${BLUE}=== $1 ===${NC}\n"
}

# Function to check if file exists and is non-empty
check_file() {
    local file="$1"
    local desc="$2"
    if [ -f "$file" ] && [ -s "$file" ]; then
        echo -e "${GREEN}✓ Found $desc: $file${NC}"
        return 0
    else
        echo -e "${RED}✗ Missing or empty $desc: $file${NC}"
        return 1
    fi
}

# Function to verify test environment
verify_test_env() {
    print_header "Verifying Test Environment"
    local missing=0
    
    # Check build configuration
    check_file "build.gradle.kts" "build configuration" || missing=$((missing + 1))
    
    # Check test configurations
    check_file "src/test/resources/application-test.yml" "test configuration" || missing=$((missing + 1))
    check_file "src/test/resources/db/h2-schema.sql" "H2 schema" || missing=$((missing + 1))
    
    # Check key test files
    check_file "src/test/kotlin/org/blackerp/config/TestConfig.kt" "test configuration" || missing=$((missing + 1))
    check_file "src/test/kotlin/org/blackerp/shared/TestFactory.kt" "test factory" || missing=$((missing + 1))
    
    # Verify test dependencies in build.gradle.kts
    print_header "Verifying Test Dependencies"
    
    local deps=(
        "io.kotest:kotest-runner-junit5"
        "io.kotest:kotest-assertions-core"
        "io.kotest.extensions:kotest-extensions-spring"
        "org.springframework.boot:spring-boot-starter-test"
        "com.ninja-squad:springmockk"
    )
    
    for dep in "${deps[@]}"; do
        if grep -q "$dep" "build.gradle.kts"; then
            echo -e "${GREEN}✓ Found dependency: $dep${NC}"
        else
            echo -e "${RED}✗ Missing dependency: $dep${NC}"
            missing=$((missing + 1))
        fi
    done
    
    # Check integration test structure
    print_header "Verifying Integration Tests"
    
    local tests=(
        "src/test/kotlin/org/blackerp/integration/api/TableApiIntegrationTest.kt"
        "src/test/kotlin/org/blackerp/integration/db/TableRepositoryIntegrationTest.kt"
        "src/test/kotlin/org/blackerp/integration/plugin/PluginLifecycleIntegrationTest.kt"
    )
    
    for test in "${tests[@]}"; do
        if check_file "$test" "integration test"; then
            echo -e "${GREEN}  ├─ Test file exists and has content${NC}"
        else
            echo -e "${YELLOW}  ├─ Test file missing or empty - may need implementation${NC}"
        fi
    done
    
    # Summary
    print_header "Verification Summary"
    if [ $missing -eq 0 ]; then
        echo -e "${GREEN}All test components verified successfully!${NC}"
        echo -e "\nNext steps:"
        echo "1. Run tests: ./gradlew test"
        echo "2. Check test coverage: ./gradlew jacocoTestReport"
        echo "3. Review test results in build/reports/tests/test/index.html"
    else
        echo -e "${RED}Found $missing missing or incomplete components${NC}"
        echo -e "\nRecommended actions:"
        echo "1. Review the output above"
        echo "2. Implement missing components"
        echo "3. Run this script again to verify"
    fi
}

# Main execution
main() {
    print_header "Starting Test Environment Verification"
    verify_test_env
}

# Execute main function
main
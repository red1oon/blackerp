#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Starting Black ERP project setup...${NC}"

# Clean existing files
echo -e "${BLUE}Cleaning existing files...${NC}"
rm -rf src/
rm -rf build/
rm -rf .gradle/
rm -rf .idea/
rm -rf .vscode/
rm -f build.gradle.kts
rm -f settings.gradle.kts
rm -f docker-compose.yml

# Create directory structure
echo -e "${BLUE}Creating directory structure...${NC}"
mkdir -p src/main/kotlin/org/blackerp/{domain/{values,common},application,infrastructure,shared}
mkdir -p src/test/kotlin/org/blackerp/{domain,application,infrastructure,shared}
mkdir -p src/main/resources
mkdir -p src/test/resources

# Create settings.gradle.kts
echo -e "${BLUE}Creating settings.gradle.kts...${NC}"
cat > settings.gradle.kts << 'EOF'
rootProject.name = "blackerp"
EOF

# Create build.gradle.kts
echo -e "${BLUE}Creating build.gradle.kts...${NC}"
cat > build.gradle.kts << 'EOF'
plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.blackerp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Arrow for functional programming
    implementation("io.arrow-kt:arrow-core:1.2.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")
    
    // Spring minimal
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
EOF

# Create domain models
echo -e "${BLUE}Creating domain models...${NC}"

# ValidationError
cat > src/main/kotlin/org/blackerp/shared/ValidationError.kt << 'EOF'
package org.blackerp.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val details: String) : ValidationError(details)
    data class Required(val field: String) : ValidationError("Field $field is required")
    data class InvalidLength(val field: String, val min: Int, val max: Int) : 
        ValidationError("Field $field must be between $min and $max characters")
}
EOF

# EntityMetadata
cat > src/main/kotlin/org/blackerp/domain/EntityMetadata.kt << 'EOF'
package org.blackerp.domain

import java.time.Instant
import java.util.UUID

data class EntityMetadata(
    val id: UUID = UUID.randomUUID(),
    val created: Instant = Instant.now(),
    val createdBy: String,
    val updated: Instant = Instant.now(),
    val updatedBy: String,
    val version: Int = 0,
    val active: Boolean = true
)
EOF

# DomainEntity interface
cat > src/main/kotlin/org/blackerp/domain/DomainEntity.kt << 'EOF'
package org.blackerp.domain

interface DomainEntity {
    val metadata: EntityMetadata
}
EOF

# TableName value object
cat > src/main/kotlin/org/blackerp/domain/values/TableName.kt << 'EOF'
package org.blackerp.domain.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class TableName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, TableName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                    ValidationError.InvalidFormat("Table name must start with lowercase letter and contain only lowercase letters, numbers, and underscores").left()
                else -> TableName(value).right()
            }
    }
}
EOF

# DisplayName value object
cat > src/main/kotlin/org/blackerp/domain/values/DisplayName.kt << 'EOF'
package org.blackerp.domain.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class DisplayName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, DisplayName> =
            when {
                value.isBlank() -> 
                    ValidationError.Required("display name").left()
                value.length !in 1..60 ->
                    ValidationError.InvalidLength("display name", 1, 60).left()
                else -> DisplayName(value).right()
            }
    }
}
EOF

# Description value object
cat > src/main/kotlin/org/blackerp/domain/values/Description.kt << 'EOF'
package org.blackerp.domain.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class Description private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, Description> =
            when {
                value.length > 255 ->
                    ValidationError.InvalidLength("description", 0, 255).left()
                else -> Description(value).right()
            }
    }
}
EOF

# Create application.yml
echo -e "${BLUE}Creating application.yml...${NC}"
cat > src/main/resources/application.yml << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/blackerp
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true
EOF

# Create test application.yml
cat > src/test/resources/application.yml << 'EOF'
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
EOF

# Create Docker Compose file
echo -e "${BLUE}Creating docker-compose.yml...${NC}"
cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  db:
    image: postgres:14-alpine
    environment:
      POSTGRES_DB: blackerp
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
EOF

# Create test helper
echo -e "${BLUE}Creating test helpers...${NC}"
cat > src/test/kotlin/org/blackerp/shared/TestFactory.kt << 'EOF'
package org.blackerp.shared

import org.blackerp.domain.EntityMetadata
import java.time.Instant
import java.util.UUID

object TestFactory {
    fun createMetadata(
        id: UUID = UUID.randomUUID(),
        createdBy: String = "test-user",
        updatedBy: String = "test-user"
    ) = EntityMetadata(
        id = id,
        created = Instant.now(),
        createdBy = createdBy,
        updated = Instant.now(),
        updatedBy = updatedBy
    )
}
EOF

# Create .gitignore if it doesn't exist
if [ ! -f .gitignore ]; then
    echo -e "${BLUE}Creating .gitignore...${NC}"
    cat > .gitignore << 'EOF'
HELP.md
.gradle
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/main/**/build/
!**/src/test/**/build/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache
bin/
!**/src/main/**/bin/
!**/src/test/**/bin/

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
out/
!**/src/main/**/out/
!**/src/test/**/out/

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/

### VS Code ###
.vscode/

### Mac OS ###
.DS_Store
EOF
fi

# Make gradlew executable
chmod +x gradlew

echo -e "${GREEN}Project setup complete!${NC}"
echo -e "${BLUE}Next steps:${NC}"
echo "1. Review and adjust the generated files"
echo "2. Run './gradlew build' to verify the setup"
echo "3. Start docker-compose with 'docker-compose up -d'"
echo "4. Begin implementing your first domain model"
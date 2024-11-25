#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Creating domain layer files...${NC}"

# Create domain directories
mkdir -p src/main/kotlin/org/blackerp/domain
mkdir -p src/main/kotlin/org/blackerp/domain/table
mkdir -p src/main/kotlin/org/blackerp/domain/values
mkdir -p src/main/kotlin/org/blackerp/shared

# File: src/main/kotlin/org/blackerp/domain/DomainEvent.kt
cat > src/main/kotlin/org/blackerp/domain/DomainEvent.kt << 'EOF'
package org.blackerp.domain

import org.blackerp.infrastructure.event.EventMetadata

interface DomainEvent {
    val metadata: EventMetadata
}
EOF

# File: src/main/kotlin/org/blackerp/domain/DomainEntity.kt
cat > src/main/kotlin/org/blackerp/domain/DomainEntity.kt << 'EOF'
package org.blackerp.domain

interface DomainEntity {
    val metadata: EntityMetadata
}
EOF

# File: src/main/kotlin/org/blackerp/domain/EntityMetadata.kt
cat > src/main/kotlin/org/blackerp/domain/EntityMetadata.kt << 'EOF'
package org.blackerp.domain

import java.time.Instant
import java.util.UUID
import org.blackerp.shared.TimeBasedId

data class EntityMetadata(
    val id: UUID = TimeBasedId.generate(),
    val created: Instant = Instant.now(),
    val createdBy: String,
    val updated: Instant = Instant.now(),
    val updatedBy: String,
    val version: Int = 0,
    val active: Boolean = true
)
EOF

# File: src/main/kotlin/org/blackerp/shared/ValidationError.kt
cat > src/main/kotlin/org/blackerp/shared/ValidationError.kt << 'EOF'
package org.blackerp.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val details: String) : ValidationError(details)
    data class Required(val field: String) : ValidationError("Field $field is required")
    data class InvalidLength(val field: String, val min: Int, val max: Int) : 
        ValidationError("Field $field must be between $min and $max characters")
}
EOF

# File: src/main/kotlin/org/blackerp/domain/values/AccessLevel.kt
cat > src/main/kotlin/org/blackerp/domain/values/AccessLevel.kt << 'EOF'
package org.blackerp.domain.values

enum class AccessLevel {
    SYSTEM,
    CLIENT,
    ORGANIZATION,
    USER
}
EOF

# File: src/main/kotlin/org/blackerp/domain/values/TableName.kt
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

# File: src/main/kotlin/org/blackerp/domain/values/DisplayName.kt
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

# File: src/main/kotlin/org/blackerp/domain/values/Description.kt
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

# File: src/main/kotlin/org/blackerp/domain/table/TableError.kt
cat > src/main/kotlin/org/blackerp/domain/table/TableError.kt << 'EOF'
package org.blackerp.domain.table

import org.blackerp.shared.ValidationError

sealed interface TableError {
    data class ValidationFailed(val errors: List<ValidationError>) : TableError
    data class StorageError(val cause: Throwable) : TableError
    data class DuplicateTable(val name: String) : TableError
    data class NotFound(val id: String) : TableError
}
EOF

# File: src/main/kotlin/org/blackerp/domain/table/ADTable.kt
cat > src/main/kotlin/org/blackerp/domain/table/ADTable.kt << 'EOF'
package org.blackerp.domain.table

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.DomainEntity
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.values.TableName
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import org.blackerp.domain.values.AccessLevel

data class ADTable(
    override val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel
) : DomainEntity {
    companion object {
        fun create(params: CreateTableParams): Either<TableError, ADTable> =
            ADTable(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                accessLevel = params.accessLevel
            ).right()
    }
}

data class CreateTableParams(
    val metadata: EntityMetadata,
    val name: TableName,
    val displayName: DisplayName,
    val description: Description?,
    val accessLevel: AccessLevel
)
EOF

# File: src/main/kotlin/org/blackerp/domain/table/TableCreated.kt
cat > src/main/kotlin/org/blackerp/domain/table/TableCreated.kt << 'EOF'
package org.blackerp.domain.table

import java.util.UUID
import org.blackerp.domain.DomainEvent
import org.blackerp.infrastructure.event.EventMetadata

data class TableCreated(
    override val metadata: EventMetadata,
    val tableId: UUID,
    val createdBy: String
) : DomainEvent
EOF

# File: src/main/kotlin/org/blackerp/shared/TimeBasedId.kt
cat > src/main/kotlin/org/blackerp/shared/TimeBasedId.kt << 'EOF'
package org.blackerp.shared

import java.util.UUID
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.impl.TimeBasedGenerator

object TimeBasedId {
    private val timeBasedGenerator: TimeBasedGenerator = Generators.timeBasedGenerator()
    
    fun generate(): UUID = timeBasedGenerator.generate()
}
EOF

echo -e "${GREEN}Domain files created successfully!${NC}"
echo "Next steps:"
echo "1. Add UUID dependency to build.gradle.kts"
echo "2. Create application layer files"
echo "3. Run './gradlew build' to verify compilation"

# Make the script executable
chmod +x create_domain_files.sh
# In test-import 

# Create core entity interfaces first
cat > domain/core/DomainEntity.kt << EOL
package org.blackerp.domain.core

interface DomainEntity {
    val metadata: EntityMetadata
}
EOL

cat > domain/core/metadata/EntityMetadata.kt << EOL
package org.blackerp.domain.core.metadata
import java.util.UUID
import java.time.Instant

data class EntityMetadata(
    val id: UUID = UUID.randomUUID(),
    val created: Instant = Instant.now(),
    val createdBy: String
)
EOL

# Now create ValidationError 
cat > domain/core/shared/ValidationError.kt << EOL
package org.blackerp.domain.core.shared

sealed class ValidationError(val message: String) {
    data class InvalidFormat(val error: String) : ValidationError(error)
}
EOL

cat > domain/core/values/TableName.kt << EOL
package org.blackerp.domain.core.values

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.domain.core.shared.ValidationError
import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.metadata.EntityMetadata

class TableName(override val metadata: EntityMetadata) : DomainEntity {
    companion object {
        fun create(value: String): Either<ValidationError, TableName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*\$")) ->
                    ValidationError.InvalidFormat("Invalid table name").left()
                else -> TableName(EntityMetadata(createdBy = "system")).right()  
            }
    }
}
EOL

./gradlew :domain:build --stacktrace
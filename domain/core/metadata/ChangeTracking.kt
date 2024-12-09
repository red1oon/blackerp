package org.blackerp.domain.core.metadata

import java.time.Instant
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo

interface ChangeTrackable {
    val metadata: EntityMetadata
    fun withUpdatedBy(user: String): ChangeTrackable
}

fun EntityMetadata.update(user: String): EntityMetadata {
    return copy(
        audit = audit.copy(
            updatedAt = Instant.now(),
            updatedBy = user
        ),
        version = version.copy(version = version.version + 1)
    )
}

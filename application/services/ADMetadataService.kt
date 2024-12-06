package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.metadata.EntityMetadata
import org.blackerp.domain.core.metadata.AuditInfo
import org.blackerp.domain.core.metadata.VersionInfo
import org.blackerp.domain.core.error.TableError
import org.blackerp.domain.core.security.SecurityContext
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import java.time.Instant

@Service
class ADMetadataService(private val securityContext: SecurityContext) {

    @Transactional
    fun generateMetadata(tableName: String): Either<TableError, EntityMetadata> {
        val currentUser = securityContext.user.username
        
        return EntityMetadata(
            id = UUID.randomUUID().toString(),
            audit = AuditInfo(
                createdBy = currentUser,
                updatedBy = currentUser
            ),
            version = VersionInfo()
        ).right()
    }

    @Transactional
    fun incrementVersion(metadata: EntityMetadata): Either<TableError, EntityMetadata> {
        val currentUser = securityContext.user.username
        
        return metadata.copy(
            audit = metadata.audit.copy(
                updatedBy = currentUser,
                updatedAt = Instant.now()
            ),
            version = metadata.version.copy(
                version = metadata.version.version + 1
            )
        ).right()
    }
}

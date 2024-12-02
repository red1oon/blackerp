package org.blackerp.application.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.core.error.TableError
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import java.time.Instant
import org.slf4j.LoggerFactory

@Service
class ADMetadataService(
    private val securityContext: SecurityContext
) {
    private val logger = LoggerFactory.getLogger(ADMetadataService::class.java)

    @Transactional
    fun generateMetadata(tableName: String): Either<TableError, EntityMetadata> {
        logger.debug("Generating metadata for table: $tableName")
        
        val currentUser = securityContext.getCurrentUser()
            ?: return TableError.InvalidMetadata(
                message = "No authenticated user found",
                field = "createdBy"
            ).left()

        return try {
            EntityMetadata(
                id = UUID.randomUUID(),
                created = Instant.now(),
                createdBy = currentUser.username,
                updated = Instant.now(),
                updatedBy = currentUser.username,
                version = 1,
                active = true
            ).right()
        } catch (e: Exception) {
            logger.error("Failed to generate metadata for table: $tableName", e)
            TableError.InvalidMetadata(
                message = "Failed to generate metadata: ${e.message}",
                field = "metadata"
            ).left()
        }
    }

    @Transactional
    fun incrementVersion(metadata: EntityMetadata): Either<TableError, EntityMetadata> {
        val currentUser = securityContext.getCurrentUser()
            ?: return TableError.InvalidMetadata(
                message = "No authenticated user found",
                field = "updatedBy"
            ).left()

        return metadata.copy(
            version = metadata.version + 1,
            updated = Instant.now(),
            updatedBy = currentUser.username
        ).right()
    }
}

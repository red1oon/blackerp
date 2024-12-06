package org.blackerp.domain.core.security

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.springframework.stereotype.Component

sealed class AccessControlError {
    data class PermissionDenied(val message: String) : AccessControlError()
    data class ValidationFailed(val message: String) : AccessControlError()
}

@Component
class DocumentAccessControl {
    suspend fun checkAccess(
        context: SecurityContext,
        documentId: UUID,
        requiredPermission: String
    ): Either<AccessControlError, Unit> {
        // Check basic permission
        if (!context.hasPermission(requiredPermission)) {
            return AccessControlError.PermissionDenied(
                "Missing required permission: $requiredPermission"
            ).left()
        }

        // TODO: Add additional access control rules based on:
        // - Document ownership
        // - Organization hierarchy
        // - Document type specific rules
        
        return Unit.right()
    }

    suspend fun checkBulkAccess(
        context: SecurityContext,
        documentIds: List<UUID>,
        requiredPermission: String
    ): Map<UUID, Either<AccessControlError, Unit>> =
        documentIds.associateWith { documentId ->
            checkAccess(context, documentId, requiredPermission)
        }
}

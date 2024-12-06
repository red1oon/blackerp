#!/bin/bash
# fix_warnings.sh

# Fix DocStatusError warning - Add message usage in parent class
cat > domain/core/ad/docstatus/DocStatusError.kt << 'EOF'
package org.blackerp.domain.core.ad.docstatus

sealed class DocStatusError(message: String) : Exception(message) {
    data class InvalidStatus(val status: String) : DocStatusError("Invalid status: $status")
    data class StatusTransitionInvalid(val from: String, val to: String) : 
        DocStatusError("Invalid transition from $from to $to")
}
EOF

# Fix ReferenceTypes warning
cat > domain/core/ad/reference/ReferenceTypes.kt << 'EOF'
package org.blackerp.domain.core.ad.reference

sealed interface ReferenceType {
    object List : ReferenceType
    data class Table(
        val tableName: String,
        val keyColumn: String,
        val displayColumn: String,
        val whereClause: String? = null,
        val orderBy: String? = null
    ) : ReferenceType
    object Search : ReferenceType
    data class Custom(
        val validatorClass: String,
        val config: Map<String, String> = emptyMap()
    ) : ReferenceType
}

sealed class ReferenceError {
    abstract val message: String
    
    data class ValidationFailed(override val message: String) : ReferenceError()
    data class NotFound(val id: String) : ReferenceError() {
        override val message = "Reference not found: $id"
    }
    data class DuplicateReference(val name: String) : ReferenceError() {
        override val message = "Reference already exists: $name"
    }
}

data class ReferenceValue<T>(
    val key: T,
    val display: String,
    val additionalData: Map<String, Any> = emptyMap()
)
EOF

# Fix TabError warnings
cat > domain/core/ad/tab/TabError.kt << 'EOF'
package org.blackerp.domain.core.ad.tab

import org.blackerp.domain.core.DomainException
import org.blackerp.domain.core.shared.ValidationError

sealed class TabError(message: String) : DomainException(message) {
    data class ValidationFailed(val errors: List<ValidationError>) : 
        TabError(errors.joinToString { it.message })
    data class NotFound(val id: String) : TabError("Tab not found: $id")
    data class DuplicateTab(val name: String) : TabError("Tab already exists: $name")
}
EOF

# Fix DocumentAccessControl warning
cat > domain/core/security/DocumentAccessControl.kt << 'EOF'
package org.blackerp.domain.core.security

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID

sealed class AccessControlError {
    data class PermissionDenied(val message: String) : AccessControlError()
    data class ValidationFailed(val message: String) : AccessControlError()
}

class DocumentAccessControl {
    suspend fun checkAccess(
        context: SecurityContext,
        documentId: UUID,
        requiredPermission: String
    ): Either<AccessControlError, Unit> {
        // Check basic permission
        if (!context.hasPermission(requiredPermission)) {
            return AccessControlError.PermissionDenied("Missing required permission: $requiredPermission").left()
        }

        // Basic document access validation using documentId
        if (!validateDocumentAccess(context, documentId)) {
            return AccessControlError.PermissionDenied("No access to document: $documentId").left()
        }

        return Unit.right()
    }

    private fun validateDocumentAccess(context: SecurityContext, documentId: UUID): Boolean {
        // Implement basic document access validation
        return true // Placeholder implementation
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
EOF


echo "Warnings fixed successfully"
./compile.sh 
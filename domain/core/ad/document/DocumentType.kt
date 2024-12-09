package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.ad.base.ADObject
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID

/**
 * Represents a Document Type that defines the structure and behavior of business documents
 * such as Sales Orders, Purchase Orders, Invoices etc.
 */
data class DocumentType(
    override val metadata: EntityMetadata,
    private val uuid: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val name: String,
    val baseTableId: UUID,
    val linesTableId: UUID?,  // For header-line type documents
    val workflowId: UUID?,    // Optional workflow definition
    val statusConfig: DocumentStatusConfig,
    val isSOTrx: Boolean = false,
    val isActive: Boolean = true
) : ADObject {
    override val id: String 
        get() = uuid.toString()

    fun validateStatusTransition(from: String, to: String): Boolean =
        statusConfig.validateTransition(from, to)

    companion object {
        fun create(
            metadata: EntityMetadata,
            displayName: DisplayName,
            description: Description?,
            name: String,
            baseTableId: UUID,
            linesTableId: UUID? = null,
            workflowId: UUID? = null,
            statusConfig: DocumentStatusConfig,
            isSOTrx: Boolean = false
        ): Either<DocumentError, DocumentType> {
            // Validate status configuration
            if (!statusConfig.isValid()) {
                return DocumentError.ValidationFailed("Invalid status configuration").left()
            }

            return DocumentType(
                metadata = metadata,
                displayName = displayName,
                description = description,
                name = name,
                baseTableId = baseTableId,
                linesTableId = linesTableId,
                workflowId = workflowId,
                statusConfig = statusConfig,
                isSOTrx = isSOTrx
            ).right()
        }
    }
}

/**
 * Defines the valid statuses and transitions for a document type
 */
data class DocumentStatusConfig(
    val statuses: Map<String, DocumentStatusDef>,
    val defaultStatus: String,
    val closingStatuses: Set<String>
) {
    fun validateTransition(from: String, to: String): Boolean {
        val fromStatus = statuses[from] ?: return false
        return fromStatus.nextStatuses.contains(to)
    }

    fun isValid(): Boolean {
        if (statuses.isEmpty()) return false
        if (!statuses.containsKey(defaultStatus)) return false
        if (closingStatuses.any { !statuses.containsKey(it) }) return false
        
        // Validate that all next statuses exist
        return statuses.values.all { status ->
            status.nextStatuses.all { statuses.containsKey(it) }
        }
    }
}

/**
 * Defines a single document status with its properties
 */
data class DocumentStatusDef(
    val code: String,
    val name: String,
    val description: String?,
    val nextStatuses: Set<String>
)

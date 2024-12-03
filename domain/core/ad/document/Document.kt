package org.blackerp.domain.core.ad.document

import org.blackerp.domain.core.EntityMetadata
import org.blackerp.domain.core.ad.ADObject
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID
import java.time.Instant

data class Document(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: DocumentType,
    val status: DocumentStatus,
    val lines: List<DocumentLine>,
    val baseDocument: UUID? = null,
    val version: Int = 1,
    val workflow: DocumentWorkflow? = null,
    val accessLevel: AccessLevel = AccessLevel.ORGANIZATION,
    val created: Instant = Instant.now(),
    val lastModified: Instant = Instant.now(),
    val archived: Boolean = false
) : ADObject {
    companion object {
        fun create(params: CreateDocumentParams): Either<DocumentError, Document> =
            Document(
                metadata = params.metadata,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                status = DocumentStatus.DRAFT,
                lines = params.lines,
                baseDocument = params.baseDocument,
                workflow = params.workflow
            ).right()
    }
}

data class DocumentLine(
    val id: UUID = UUID.randomUUID(),
    val lineNo: Int,
    val attributes: Map<String, Any>,
    val referencedDocument: UUID? = null,
    val status: LineStatus = LineStatus.ACTIVE
)

data class DocumentType(
    val id: UUID,
    val code: String,
    val name: String,
    val baseTable: String,
    val linesTable: String?,
    val workflow: UUID?,
    val numberingPattern: String,
    val attributes: Map<String, AttributeDefinition>
)

data class AttributeDefinition(
    val name: String,
    val type: AttributeType,
    val referenceId: UUID?,
    val mandatory: Boolean = false,
    val defaultValue: Any? = null,
    val validationRule: String? = null
)

enum class AttributeType {
    STRING, NUMBER, DATE, BOOLEAN, REFERENCE, DOCUMENT
}

data class DocumentWorkflow(
    val currentState: String,
    val availableTransitions: List<String>,
    val assignee: String?,
    val dueDate: Instant?
)

enum class DocumentStatus {
    DRAFT, IN_PROGRESS, COMPLETED, CANCELLED, VOID
}

enum class LineStatus {
    ACTIVE, CANCELLED, COMPLETED
}

enum class AccessLevel {
    SYSTEM, CLIENT, ORGANIZATION, CLIENT_ORGANIZATION
}

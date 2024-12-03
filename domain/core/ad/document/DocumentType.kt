package org.blackerp.domain.core.ad.document

import java.util.UUID

data class DocumentType(
    val id: UUID,
    val name: String,
    val baseTableId: UUID,
    val linesTableId: UUID?,
    val workflow: UUID?
)

data class DocumentLine(
    val id: UUID,
    val documentId: UUID,
    val lineNo: Int,
    val attributes: Map<String, Any>
)

package org.blackerp.application.usecases.document

import org.blackerp.domain.core.ad.document.DocumentType

data class CreateDocumentCommand(
   val displayName: String,
   val description: String?,
   val documentType: DocumentType
)

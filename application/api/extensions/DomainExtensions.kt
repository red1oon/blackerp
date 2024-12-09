package org.blackerp.application.api.extensions

import org.blackerp.domain.core.ad.document.Document
import org.blackerp.domain.core.ad.process.ADProcess
import org.blackerp.application.api.dto.requests.CreateDocumentRequest
import org.blackerp.application.api.dto.requests.CreateProcessRequest
import org.blackerp.domain.core.error.DomainError
import arrow.core.Either
import arrow.core.right

fun CreateDocumentRequest.toDomain(): Either<DomainError, Document> = TODO()
fun CreateProcessRequest.toDomain(): Either<DomainError, ADProcess> = TODO()

package org.blackerp.domain.core.ad.metadata.services

import org.blackerp.domain.core.ad.metadata.entities.*
import kotlinx.coroutines.flow.Flow
import arrow.core.Either
import java.util.UUID

interface MetadataService {
    suspend fun getRules(entityType: String): Flow<ADRule>
    suspend fun getRule(id: UUID): Either<MetadataError, ADRule?>
    suspend fun getValidations(entityType: String): Flow<ADValidationRule>
    suspend fun getStatusFlow(documentType: String): Flow<ADStatusLine>
}

sealed class MetadataError(message: String) {
    data class NotFound(val id: String) : MetadataError("Metadata not found: $id")
    data class ValidationFailed(val details: String) : MetadataError(details)
    data class AccessDenied(val details: String) : MetadataError(details)
}

// Add new interpretation methods to existing interface
interface MetadataInterpreter<T> {
    suspend fun interpret(metadata: T, context: Map<String, Any>): Either<MetadataError, InterpretationResult>
}

sealed class InterpretationResult {
    data class ValidationResult(val isValid: Boolean, val messages: List<String>) : InterpretationResult()
    data class WindowResult(val components: List<Component>) : InterpretationResult()
    data class WorkflowResult(val nextStates: List<String>) : InterpretationResult()
}

// Basic component model for UI metadata
data class Component(
    val id: String,
    val type: String,
    val properties: Map<String, Any>
)

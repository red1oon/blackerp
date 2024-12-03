package org.blackerp.domain.core.ad.reference

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.core.EntityMetadata
import org.blackerp.domain.core.ad.ADObject
import org.blackerp.domain.core.ad.reference.value.ReferenceName
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description
import org.blackerp.domain.core.shared.ValidationError
import java.util.UUID

data class ADReference(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    val name: ReferenceName,
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule?,
    val isActive: Boolean = true,
    val parentId: UUID? = null,
    val sortOrder: Int = 0,
    val cacheStrategy: CacheStrategy = CacheStrategy.NONE
) : ADObject {
    companion object {
        fun create(params: CreateReferenceParams): Either<ReferenceError, ADReference> =
            ADReference(
                metadata = params.metadata,
                name = params.name,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                validationRule = params.validationRule,
                parentId = params.parentId,
                sortOrder = params.sortOrder,
                cacheStrategy = params.cacheStrategy
            ).right()
    }
}

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

data class ValidationRule(
    val expression: String,
    val errorMessage: String,
    val parameters: Map<String, String> = emptyMap()
)

enum class CacheStrategy {
    NONE,
    SESSION,
    APPLICATION,
    TIMED
}

data class CreateReferenceParams(
    val metadata: EntityMetadata,
    val name: ReferenceName,
    val displayName: DisplayName,
    val description: Description?,
    val type: ReferenceType,
    val validationRule: ValidationRule? = null,
    val parentId: UUID? = null,
    val sortOrder: Int = 0,
    val cacheStrategy: CacheStrategy = CacheStrategy.NONE
)

sealed class ReferenceError {
    data class ValidationFailed(val errors: List<ValidationError>) : ReferenceError()
    data class DuplicateReference(val name: String) : ReferenceError()
    data class ReferenceNotFound(val name: String) : ReferenceError()
    data class CircularReference(val path: List<String>) : ReferenceError()
    data class InvalidConfiguration(val message: String) : ReferenceError()
}

data class ReferenceValue<T>(
    val key: T,
    val display: String,
    val additionalData: Map<String, Any> = emptyMap()
)

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

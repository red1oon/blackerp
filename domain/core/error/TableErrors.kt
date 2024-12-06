package org.blackerp.domain.core.error

sealed class TableError : DomainError("Table operation failed") {
    data class ValidationError(
        override val message: String,
        val violations: List<Violation>
    ) : TableError()

    data class ConstraintViolation(
        override val message: String,
        val constraintName: String,
        val details: String
    ) : TableError()

    data class DatabaseError(
        override val message: String,
        val sqlState: String?,
        val errorCode: Int?
    ) : TableError()

    data class InvalidMetadata(
        override val message: String,
        val field: String
    ) : TableError()

    data class Violation(
        val field: String,
        val message: String,
        val value: Any? = null
    )
}

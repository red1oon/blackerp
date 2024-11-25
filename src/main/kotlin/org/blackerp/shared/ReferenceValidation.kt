package org.blackerp.shared

sealed interface ReferenceValidation {
    data class ColumnNotFound(val columnName: String) : ValidationError("Column not found: $columnName")
    data class ReferenceTableNotFound(val tableName: String) : ValidationError("Reference table not found: $tableName")
    data class ReferenceColumnNotFound(
        val tableName: String,
        val columnName: String
    ) : ValidationError("Column $columnName not found in table $tableName")
    data class IncompatibleTypes(
        val sourceColumn: String,
        val sourceType: String,
        val targetColumn: String,
        val targetType: String
    ) : ValidationError("Incompatible types: $sourceColumn ($sourceType) cannot reference $targetColumn ($targetType)")
}
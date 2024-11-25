package org.blackerp.domain.table.relationship

import org.blackerp.shared.ValidationError

sealed interface TableRelationshipError {
    data class ValidationFailed(val errors: List<ValidationError>) : TableRelationshipError
    data class DuplicateRelationship(val name: String) : TableRelationshipError
    data class InvalidRelationType(val message: String) : TableRelationshipError
    data class CircularReference(val tableName: String) : TableRelationshipError
}

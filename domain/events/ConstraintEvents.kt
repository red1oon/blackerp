package org.blackerp.domain.events

import java.util.UUID
import org.blackerp.domain.core.ad.table.ConstraintType

sealed class ConstraintEvent : DomainEvent {
    data class ConstraintCreated(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val name: String,
        val type: ConstraintType,
        val columns: List<String>,
        val expression: String?
    ) : ConstraintEvent()

    data class ConstraintModified(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val previousName: String,
        val newName: String,
        val previousColumns: List<String>,
        val newColumns: List<String>,
        val previousExpression: String?,
        val newExpression: String?
    ) : ConstraintEvent()

    data class ConstraintDeleted(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val name: String
    ) : ConstraintEvent()

    data class ConstraintViolated(
        override val metadata: EventMetadata,
        val constraintId: UUID,
        val tableId: UUID,
        val violationType: ViolationType,
        val violationDetails: String
    ) : ConstraintEvent()
}

enum class ViolationType {
    UNIQUE_VIOLATION,
    CHECK_VIOLATION,
    FOREIGN_KEY_VIOLATION
}

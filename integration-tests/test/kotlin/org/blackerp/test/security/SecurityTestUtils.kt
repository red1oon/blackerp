package org.blackerp.test.security

import org.blackerp.domain.core.ad.metadata.entities.ADRule
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.values.DisplayName
import java.util.UUID

object SecurityTestUtils {
    fun createTestRule(
        entityType: String,
        expression: String,
        errorMessage: String? = null
    ): ADRule {
        val metadata = EntityMetadata(
            id = UUID.randomUUID().toString(),
            audit = AuditInfo(
                createdBy = "test-user",
                updatedBy = "test-user"
            )
        )

        return ADRule(
            metadata = metadata,
            displayName = DisplayName.create("Test Rule")
                .fold({ throw IllegalArgumentException(it.message) }, { it }),
            description = null,
            ruleType = "VALIDATION",
            entityType = entityType,
            expression = expression,
            errorMessage = errorMessage
        )
    }
}

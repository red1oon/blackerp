package org.blackerp.domain.core.ad.process

import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description

enum class ProcessType {
    REPORT, CALCULATION, SYNCHRONIZATION, WORKFLOW, DATA_IMPORT, DATA_EXPORT, CUSTOM
}

data class CreateProcessParams(
    val metadata: EntityMetadata,
    val displayName: DisplayName,
    val description: Description?,
    val type: ProcessType,
    val parameters: List<ProcessParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?
)

data class ProcessParameter(
    val id: java.util.UUID = java.util.UUID.randomUUID(),
    val name: String,
    val displayName: String,
    val description: String?,
    val parameterType: ParameterType,
    val isMandatory: Boolean = false,
    val validationRule: String?
)

enum class ParameterType {
    STRING, NUMBER, DATE, BOOLEAN, REFERENCE, FILE
}

data class ProcessResult(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any>? = null,
    val logs: List<String> = emptyList()
)

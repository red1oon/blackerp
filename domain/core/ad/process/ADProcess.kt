package org.blackerp.domain.ad.process

import org.blackerp.domain.EntityMetadata
import org.blackerp.domain.ad.ADObject
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description
import arrow.core.Either
import arrow.core.right
import java.util.UUID

data class ADProcess(
    override val metadata: EntityMetadata,
    val id: UUID = UUID.randomUUID(),
    override val displayName: DisplayName,
    override val description: Description?,
    val type: ProcessType,
    val parameters: List<ProcessParameter>,
    val implementation: ProcessImplementation,
    val schedule: ProcessSchedule?,
    val accessLevel: AccessLevel = AccessLevel.ORGANIZATION,
    val version: Int = 1,
    val status: ProcessStatus = ProcessStatus.ACTIVE
) : ADObject {
    companion object {
        fun create(params: CreateProcessParams): Either<ProcessError, ADProcess> =
            ADProcess(
                metadata = params.metadata,
                displayName = params.displayName,
                description = params.description,
                type = params.type,
                parameters = params.parameters,
                implementation = params.implementation,
                schedule = params.schedule
            ).right()
    }
}

enum class ProcessType {
    REPORT,
    CALCULATION,
    SYNCHRONIZATION,
    WORKFLOW,
    DATA_IMPORT,
    DATA_EXPORT,
    CUSTOM
}

enum class ProcessStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    DEPRECATED
}

data class ProcessParameter(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val displayName: String,
    val description: String?,
    val parameterType: ParameterType,
    val referenceId: UUID?,
    val defaultValue: String?,
    val isMandatory: Boolean = false,
    val validationRule: String?
)

enum class ParameterType {
    STRING,
    NUMBER,
    DATE,
    BOOLEAN,
    REFERENCE,
    FILE
}

sealed interface ProcessImplementation {
    data class JavaClass(
        val className: String,
        val methodName: String = "execute"
    ) : ProcessImplementation
    
    data class DatabaseFunction(
        val functionName: String,
        val schema: String = "public"
    ) : ProcessImplementation
    
    data class Script(
        val language: String,
        val code: String,
        val version: String = "1.0"
    ) : ProcessImplementation
    
    data class RestEndpoint(
        val url: String,
        val method: String,
        val headers: Map<String, String> = emptyMap(),
        val bodyTemplate: String?
    ) : ProcessImplementation
}

data class ProcessSchedule(
    val cronExpression: String,
    val timezone: String = "UTC",
    val startDate: java.time.LocalDateTime? = null,
    val endDate: java.time.LocalDateTime? = null,
    val maxExecutions: Int? = null,
    val enabled: Boolean = true
)

sealed class ProcessError {
    data class ValidationFailed(val message: String) : ProcessError()
    data class ExecutionFailed(val message: String) : ProcessError()
    data class NotFound(val id: UUID) : ProcessError()
    data class InvalidSchedule(val message: String) : ProcessError()
    data class InvalidImplementation(val message: String) : ProcessError()
}

data class ProcessResult(
    val success: Boolean,
    val message: String?,
    val data: Map<String, Any>? = null,
    val logs: List<String> = emptyList(),
    val executionTime: Long? = null
)

enum class AccessLevel {
    SYSTEM,
    CLIENT,
    ORGANIZATION,
    CLIENT_ORGANIZATION
}

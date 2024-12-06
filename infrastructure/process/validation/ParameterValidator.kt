package org.blackerp.infrastructure.process.validation

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.process.*
import org.blackerp.domain.core.error.ProcessError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory

@Component
class ParameterValidator {
    private val logger = LoggerFactory.getLogger(ParameterValidator::class.java)

    fun validate(
        parameters: Map<String, Any>,
        expectedParameters: List<ProcessParameter>
    ): Either<ProcessError, Map<String, Any>> {
        val errors = mutableListOf<String>()

        // Check required parameters
        expectedParameters.filter { it.isMandatory }
            .forEach { param ->
                if (!parameters.containsKey(param.name)) {
                    errors.add("Missing required parameter: ${param.name}")
                }
            }

        // Validate parameter types
        parameters.forEach { (name, value) ->
            expectedParameters.find { it.name == name }?.let { param ->
                if (!isValidType(value, param.parameterType)) {
                    errors.add("Invalid type for parameter $name: expected ${param.parameterType}")
                }
            } ?: errors.add("Unexpected parameter: $name")
        }

        return if (errors.isEmpty()) {
            parameters.right()
        } else {
            ProcessError.ValidationFailed(errors.joinToString(", ")).left()
        }
    }

    private fun isValidType(value: Any, type: ParameterType): Boolean = when(type) {
        ParameterType.STRING -> value is String
        ParameterType.NUMBER -> value is Number
        ParameterType.DATE -> value is java.time.temporal.Temporal
        ParameterType.BOOLEAN -> value is Boolean
        ParameterType.REFERENCE -> true // TODO: Implement reference validation
        ParameterType.FILE -> true // TODO: Implement file validation
    }
}

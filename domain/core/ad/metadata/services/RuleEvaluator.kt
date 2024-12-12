package org.blackerp.domain.core.ad.metadata.services

import arrow.core.Either
import org.blackerp.domain.core.ad.metadata.entities.ADRule
import org.blackerp.domain.core.shared.ValidationError

interface RuleEvaluator {
    suspend fun evaluate(rule: ADRule, context: Map<String, Any>): Either<ValidationError, Boolean>
    suspend fun validateParameters(rule: ADRule, parameters: Map<String, Any>): Either<ValidationError, Map<String, Any>>
    
    // Add interpretation method with default implementation
    suspend fun interpretRule(
        rule: ADRule,
        context: Map<String, Any>,
        interpreter: MetadataInterpreter<ADRule>
    ): Either<ValidationError, InterpretationResult> {
        return interpreter.interpret(rule, context)
            .mapLeft { error -> 
                when(error) {
                    is MetadataError.ValidationFailed -> ValidationError.InvalidValue(error.details)
                    is MetadataError.NotFound -> ValidationError.InvalidValue("Rule not found: ${error.id}")
                    is MetadataError.AccessDenied -> ValidationError.InvalidValue("Access denied: ${error.details}")
                }
            }
    }
}

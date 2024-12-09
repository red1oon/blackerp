package org.blackerp.domain.core.ad.metadata.services

import arrow.core.Either
import org.blackerp.domain.core.ad.metadata.entities.ADRule
import org.blackerp.domain.core.shared.ValidationError

interface RuleEvaluator {
    suspend fun evaluate(
        rule: ADRule, 
        context: Map<String, Any>
    ): Either<ValidationError, Boolean>
    
    suspend fun validateParameters(
        rule: ADRule,
        parameters: Map<String, Any>
    ): Either<ValidationError, Map<String, Any>>
}

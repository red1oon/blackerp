package org.blackerp.domain.core.ad.metadata.expression

import arrow.core.Either
import org.blackerp.domain.core.shared.ValidationError

interface ExpressionEngine {
    suspend fun evaluate(
        expression: String,
        context: Map<String, Any>
    ): Either<ValidationError, ExpressionResult>

    suspend fun validateExpression(expression: String): Either<ValidationError, Unit>
}

sealed interface ExpressionResult {
    val value: Any
    
    data class BooleanResult(override val value: Boolean) : ExpressionResult
    data class NumericResult(override val value: Number) : ExpressionResult
    data class StringResult(override val value: String) : ExpressionResult
    data class DateResult(override val value: java.time.temporal.Temporal) : ExpressionResult
}

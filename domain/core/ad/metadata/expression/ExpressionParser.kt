package org.blackerp.domain.core.ad.metadata.expression

import arrow.core.Either
import org.blackerp.domain.core.shared.ValidationError

sealed interface Token {
    data class Variable(val name: String) : Token
    data class Literal(val value: Any) : Token
    data class Operator(val symbol: String) : Token
    data class Function(val name: String) : Token
    object LeftParen : Token
    object RightParen : Token
}

interface ExpressionParser {
    fun parse(expression: String): Either<ValidationError, List<Token>>
    fun validateSyntax(tokens: List<Token>): Either<ValidationError, Unit>
}

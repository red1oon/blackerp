package org.blackerp.infrastructure.validation.expression

import org.springframework.stereotype.Component
import org.blackerp.domain.core.ad.metadata.expression.*
import org.blackerp.domain.core.shared.ValidationError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.slf4j.LoggerFactory
import java.util.*

@Component
class DefaultExpressionEngine(
    private val parser: ExpressionParser
) : ExpressionEngine {
    private val logger = LoggerFactory.getLogger(DefaultExpressionEngine::class.java)

    override suspend fun evaluate(
        expression: String,
        context: Map<String, Any>
    ): Either<ValidationError, ExpressionResult> = try {
        parser.parse(expression)
            .flatMap { tokens -> evaluateTokens(tokens, context) }
    } catch (e: Exception) {
        logger.error("Expression evaluation failed: $expression", e)
        ValidationError.InvalidValue("Expression evaluation failed: ${e.message}").left()
    }

    override suspend fun validateExpression(
        expression: String
    ): Either<ValidationError, Unit> = 
        parser.parse(expression)
            .flatMap { tokens -> parser.validateSyntax(tokens) }

    private fun evaluateTokens(
        tokens: List<Token>,
        context: Map<String, Any>
    ): Either<ValidationError, ExpressionResult> {
        val stack = Stack<Any>()
        
        for (token in tokens) {
            when (token) {
                is Token.Variable -> {
                    val value = context[token.name] ?: return ValidationError
                        .InvalidValue("Variable not found: ${token.name}")
                        .left()
                    stack.push(value)
                }
                is Token.Literal -> stack.push(token.value)
                is Token.Operator -> {
                    if (stack.size < 2) return ValidationError
                        .InvalidValue("Invalid expression: insufficient operands")
                        .left()
                    
                    val right = stack.pop()
                    val left = stack.pop()
                    val result = evaluateOperation(token.symbol, left, right)
                        ?: return ValidationError
                            .InvalidValue("Invalid operation: ${token.symbol}")
                            .left()
                    stack.push(result)
                }
                is Token.Function -> {
                    val result = evaluateFunction(token.name, stack)
                        ?: return ValidationError
                            .InvalidValue("Invalid function: ${token.name}")
                            .left()
                    stack.push(result)
                }
                else -> {} // Handle parentheses in parser
            }
        }

        if (stack.size != 1) return ValidationError
            .InvalidValue("Invalid expression: incorrect number of operands")
            .left()

        return convertToResult(stack.pop()).right()
    }

    private fun evaluateOperation(op: String, left: Any, right: Any): Any? = when (op) {
        "==" -> left == right
        "!=" -> left != right
        ">" -> compareValues(left, right) > 0
        "<" -> compareValues(left, right) < 0
        ">=" -> compareValues(left, right) >= 0
        "<=" -> compareValues(left, right) <= 0
        "+" -> add(left, right)
        "-" -> subtract(left, right)
        "*" -> multiply(left, right)
        "/" -> divide(left, right)
        "&&" -> (left as? Boolean ?: false) && (right as? Boolean ?: false)
        "||" -> (left as? Boolean ?: false) || (right as? Boolean ?: false)
        else -> null
    }

    private fun evaluateFunction(name: String, stack: Stack<Any>): Any? = when (name.lowercase()) {
        "sum" -> stack.toList().filterIsInstance<Number>().sumOf { it.toDouble() }
        "avg" -> stack.toList().filterIsInstance<Number>().let { numbers ->
            numbers.sumOf { it.toDouble() } / numbers.size
        }
        "max" -> stack.toList().filterIsInstance<Number>().maxOf { it.toDouble() }
        "min" -> stack.toList().filterIsInstance<Number>().minOf { it.toDouble() }
        else -> null
    }

    private fun convertToResult(value: Any): ExpressionResult = when (value) {
        is Boolean -> ExpressionResult.BooleanResult(value)
        is Number -> ExpressionResult.NumericResult(value)
        is String -> ExpressionResult.StringResult(value)
        is java.time.temporal.Temporal -> ExpressionResult.DateResult(value)
        else -> throw IllegalArgumentException("Unsupported result type: ${value::class.java}")
    }

    private fun add(left: Any, right: Any): Any? = when {
        left is Number && right is Number -> left.toDouble() + right.toDouble()
        left is String || right is String -> "$left$right"
        else -> null
    }

    private fun subtract(left: Any, right: Any): Any? = when {
        left is Number && right is Number -> left.toDouble() - right.toDouble()
        else -> null
    }

    private fun multiply(left: Any, right: Any): Any? = when {
        left is Number && right is Number -> left.toDouble() * right.toDouble()
        else -> null
    }

    private fun divide(left: Any, right: Any): Any? = when {
        left is Number && right is Number -> {
            if (right.toDouble() == 0.0) null
            else left.toDouble() / right.toDouble()
        }
        else -> null
    }

    @Suppress("UNCHECKED_CAST")
    private fun compareValues(left: Any, right: Any): Int = when {
        left is Number && right is Number -> 
            left.toDouble().compareTo(right.toDouble())
        left is String && right is String -> 
            left.compareTo(right)
        left is Comparable<*> && right::class.java == left::class.java ->
            (left as Comparable<Any>).compareTo(right)
        else -> throw IllegalArgumentException("Cannot compare $left with $right")
    }
}

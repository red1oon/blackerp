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
class DefaultExpressionParser : ExpressionParser {
    private val logger = LoggerFactory.getLogger(DefaultExpressionParser::class.java)

    private val operators = setOf(
        "==", "!=", ">", "<", ">=", "<=",
        "+", "-", "*", "/", "&&", "||"
    )

    private val functions = setOf(
        "sum", "avg", "max", "min"
    )

    override fun parse(expression: String): Either<ValidationError, List<Token>> = try {
        tokenize(expression)
            .flatMap { tokens -> convertToRPN(tokens) }
    } catch (e: Exception) {
        logger.error("Expression parsing failed: $expression", e)
        ValidationError.InvalidValue("Expression parsing failed: ${e.message}").left()
    }

    override fun validateSyntax(tokens: List<Token>): Either<ValidationError, Unit> {
        var stack = 0
        
        for (token in tokens) {
            when (token) {
                is Token.Operator -> {
                    stack -= 1
                    if (stack < 0) return ValidationError
                        .InvalidValue("Invalid expression: insufficient operands")
                        .left()
                }
                is Token.Function -> {
                    // Functions consume all stack items
                    if (stack <= 0) return ValidationError
                        .InvalidValue("Invalid expression: function requires arguments")
                        .left()
                }
                else -> stack += 1
            }
        }

        return if (stack == 1) Unit.right()
        else ValidationError.InvalidValue("Invalid expression: incorrect number of operands").left()
    }

    private fun tokenize(expression: String): Either<ValidationError, List<Token>> {
        val tokens = mutableListOf<Token>()
        var current = ""
        var i = 0

        while (i < expression.length) {
            when {
                expression[i].isWhitespace() -> {
                    if (current.isNotEmpty()) {
                        addToken(current, tokens)
                        current = ""
                    }
                }
                expression[i] == '(' -> {
                    if (current.isNotEmpty()) {
                        addToken(current, tokens)
                        current = ""
                    }
                    tokens.add(Token.LeftParen)
                }
                expression[i] == ')' -> {
                    if (current.isNotEmpty()) {
                        addToken(current, tokens)
                        current = ""
                    }
                    tokens.add(Token.RightParen)
                }
                isOperatorChar(expression[i]) -> {
                    if (current.isNotEmpty()) {
                        addToken(current, tokens)
                        current = ""
                    }
                    val op = parseOperator(expression, i)
                    tokens.add(Token.Operator(op))
                    i += op.length - 1
                }
                else -> current += expression[i]
            }
            i++
        }

        if (current.isNotEmpty()) {
            addToken(current, tokens)
        }

        return tokens.right()
    }

    private fun addToken(token: String, tokens: MutableList<Token>) {
        when {
            token.matches(Regex("^[0-9]+$")) -> 
                tokens.add(Token.Literal(token.toInt()))
            token.matches(Regex("^[0-9]*\\.[0-9]+$")) -> 
                tokens.add(Token.Literal(token.toDouble()))
            token.equals("true", ignoreCase = true) -> 
                tokens.add(Token.Literal(true))
            token.equals("false", ignoreCase = true) -> 
                tokens.add(Token.Literal(false))
            token in functions -> 
                tokens.add(Token.Function(token))
            else -> 
                tokens.add(Token.Variable(token))
        }
    }

    private fun convertToRPN(tokens: List<Token>): Either<ValidationError, List<Token>> {
        val output = mutableListOf<Token>()
        val operators = Stack<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Literal, is Token.Variable -> 
                    output.add(token)
                is Token.Function -> 
                    operators.push(token)
                is Token.LeftParen -> 
                    operators.push(token)
                is Token.RightParen -> {
                    while (operators.isNotEmpty() && operators.peek() !is Token.LeftParen) {
                        output.add(operators.pop())
                    }
                    if (operators.isEmpty()) return ValidationError
                        .InvalidValue("Mismatched parentheses")
                        .left()
                    operators.pop() // Remove LeftParen
                    if (operators.isNotEmpty() && operators.peek() is Token.Function) {
                        output.add(operators.pop())
                    }
                }
                is Token.Operator -> {
                    while (operators.isNotEmpty() && 
                           operators.peek() is Token.Operator &&
                           precedence(token) <= precedence(operators.peek() as Token.Operator)) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }
            }
        }

        while (operators.isNotEmpty()) {
            val op = operators.pop()
            if (op is Token.LeftParen) return ValidationError
                .InvalidValue("Mismatched parentheses")
                .left()
            output.add(op)
        }

        return output.right()
    }

    private fun precedence(op: Token.Operator): Int = when (op.symbol) {
        "*", "/" -> 5
        "+", "-" -> 4
        ">", "<", ">=", "<=" -> 3
        "==", "!=" -> 2
        "&&" -> 1
        "||" -> 0
        else -> -1
    }

    private fun isOperatorChar(c: Char): Boolean =
        c in setOf('+', '-', '*', '/', '>', '<', '=', '!', '&', '|')

    private fun parseOperator(expr: String, start: Int): String {
        val possible = expr.substring(start, minOf(start + 2, expr.length))
        return when {
            possible.length == 2 && operators.contains(possible) -> possible
            operators.contains(possible[0].toString()) -> possible[0].toString()
            else -> throw IllegalArgumentException("Invalid operator at position $start")
        }
    }
}

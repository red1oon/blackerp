package org.blackerp.api.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.blackerp.domain.DomainException
import org.blackerp.domain.core.error.TableError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.HttpStatus
import org.slf4j.LoggerFactory
import java.time.Instant

@ControllerAdvice
class ErrorHandler {
    private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    data class ErrorResponse(
        val timestamp: Instant = Instant.now(),
        val message: String,
        val details: List<String>,
        val errorType: String,
        val correlationId: String? = null,
        val stackTrace: String? = null
    )

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorResponse> {
        logger.error("Domain exception occurred", ex)
        return when(ex) {
            is TableError.ValidationFailed -> handleValidationError(ex)
            is TableError.NotFound -> handleNotFoundError(ex)
            else -> handleGenericError(ex)
        }
    }

    private fun handleValidationError(ex: TableError.ValidationFailed): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .badRequest()
            .body(ErrorResponse(
                message = "Validation failed",
                details = ex.violations.map { "${it.field}: ${it.message}" },
                errorType = "VALIDATION_ERROR"
            ))

    private fun handleNotFoundError(ex: TableError.NotFound): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                message = ex.message,
                details = emptyList(),
                errorType = "NOT_FOUND"
            ))

    private fun handleGenericError(ex: DomainException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                message = "Internal server error",
                details = listOf(ex.message ?: "Unknown error"),
                errorType = "INTERNAL_ERROR",
                stackTrace = if (isDevelopmentProfile()) ex.stackTraceToString() else null
            ))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest()
            .body(ErrorResponse(
                message = "Validation failed",
                details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" },
                errorType = "INVALID_REQUEST"
            ))

    private fun isDevelopmentProfile(): Boolean = 
        System.getProperty("spring.profiles.active")?.contains("dev") ?: false
}

package org.blackerp.api.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.blackerp.domain.DomainException
import org.blackerp.domain.table.TableError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.HttpStatus

@ControllerAdvice
class ErrorHandler {
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorResponse> =
        when(ex) {
            is TableError.ValidationFailed -> ResponseEntity.badRequest()
                .body(ErrorResponse("Validation failed", ex.errors.map { it.message }))
            is TableError.NotFound -> ResponseEntity.notFound()
                .build()
            else -> ResponseEntity.internalServerError()
                .body(ErrorResponse("Internal error", listOf(ex.message ?: ex.toString())))
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(
            ErrorResponse(
                message = "Validation failed",
                details = ex.bindingResult.fieldErrors.map { 
                    "${it.field}: ${it.defaultMessage}"
                }
            )
        )
}

data class ErrorResponse(
    val message: String,
    val details: List<String>
)

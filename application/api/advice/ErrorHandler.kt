package org.blackerp.application.api.advice

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.ResponseEntity
import org.blackerp.domain.core.error.DomainError

@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler
    fun handle(error: DomainError): ResponseEntity<Any> = 
        ResponseEntity.badRequest().body(error.message)
}

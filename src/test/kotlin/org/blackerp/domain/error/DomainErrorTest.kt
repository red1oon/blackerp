package org.blackerp.domain.error

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class DomainErrorTest : DescribeSpec({
    describe("DomainError") {
        it("should create validation error") {
            val error = DomainError.ValidationError("Invalid input", "field1")
            error.shouldBeTypeOf<DomainError.ValidationError>()
            error.message shouldBe "Invalid input"
            error.field shouldBe "field1"
        }
        
        it("should create security error") {
            val error = DomainError.SecurityError(
                message = "Access denied",
                reason = "Insufficient permissions"
            )
            error.shouldBeTypeOf<DomainError.SecurityError>()
            error.message shouldBe "Access denied"
            error.reason shouldBe "Insufficient permissions"
        }
        
        it("should create system error with cause") {
            val cause = RuntimeException("Test exception")
            val error = DomainError.SystemError("System failure", cause)
            error.shouldBeTypeOf<DomainError.SystemError>()
            error.message shouldBe "System failure"
            error.cause shouldBe cause
        }
    }
})

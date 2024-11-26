package org.blackerp.domain.values

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import java.math.BigDecimal

class AmountTest : DescribeSpec({
    describe("Amount") {
        describe("create") {
            it("should create valid amount") {
                val result = Amount.create(BigDecimal("100.00"))
                result.shouldBeRight()
            }

            it("should reject negative amount") {
                val result = Amount.create(BigDecimal("-100.00"))
                result.shouldBeLeft()
            }
        }
    }
})

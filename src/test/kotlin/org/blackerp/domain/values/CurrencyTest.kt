package org.blackerp.domain.values

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft

class CurrencyTest : DescribeSpec({
    describe("Currency") {
        describe("create") {
            it("should create valid currency code") {
                val result = Currency.create("USD")
                result.shouldBeRight()
            }

            it("should reject invalid format") {
                val result = Currency.create("usd")
                result.shouldBeLeft()
            }
        }
    }
})

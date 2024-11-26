package org.blackerp.domain.transaction

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import org.blackerp.domain.values.Amount
import org.blackerp.domain.values.Currency
import org.blackerp.shared.TestFactory
import java.math.BigDecimal
import java.time.Instant

class TransactionTest : DescribeSpec({
    describe("Transaction") {
        describe("create") {
            it("should create valid transaction") {
                val amount = Amount.create(BigDecimal("100.00")).getOrNull()!!
                val currency = Currency.create("USD").getOrNull()!!
                
                val params = CreateTransactionParams(
                    metadata = TestFactory.createMetadata(),
                    amount = amount,
                    currency = currency,
                    timestamp = Instant.now(),
                    description = "Test transaction"
                )
                
                val result = Transaction.create(params)
                result.shouldBeRight()
            }
        }
    }
})

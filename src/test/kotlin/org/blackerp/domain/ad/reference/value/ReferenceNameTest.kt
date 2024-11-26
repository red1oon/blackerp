
package org.blackerp.domain.ad.reference.value

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class ReferenceNameTest : DescribeSpec({
    describe("ReferenceName") {
        describe("create") {
            it("should create valid reference name") {
                val result = ReferenceName.create("valid_reference")
                result.shouldBeRight()
            }

            it("should reject invalid format") {
                val result = ReferenceName.create("Invalid Reference")
                result.shouldBeLeft()
            }

            it("should enforce length constraints") {
                ReferenceName.create("ab").shouldBeLeft()
                ReferenceName.create("a".repeat(51)).shouldBeLeft()
            }
        }
    }
})

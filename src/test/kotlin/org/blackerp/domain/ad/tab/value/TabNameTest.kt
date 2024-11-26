
package org.blackerp.domain.ad.tab.value

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class TabNameTest : DescribeSpec({
    describe("TabName") {
        describe("create") {
            it("should create valid tab name") {
                val result = TabName.create("valid_tab")
                result.shouldBeRight()
            }

            it("should reject invalid format") {
                val result = TabName.create("Invalid Tab")
                result.shouldBeLeft()
            }
        }
    }
})

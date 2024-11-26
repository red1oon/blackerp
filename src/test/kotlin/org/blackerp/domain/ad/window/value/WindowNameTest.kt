
package org.blackerp.domain.ad.window.value

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class WindowNameTest : DescribeSpec({
    describe("WindowName") {
        describe("create") {
            it("should create valid window name") {
                val result = WindowName.create("valid_window")
                result.shouldBeRight()
            }

            it("should reject invalid format") {
                val result = WindowName.create("Invalid Window")
                result.shouldBeLeft()
            }

            it("should enforce length constraints") {
                WindowName.create("ab").shouldBeLeft()
                WindowName.create("a".repeat(51)).shouldBeLeft()
            }
        }
    }
})

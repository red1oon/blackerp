package org.blackerp.domain.values

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import arrow.core.Either

class DisplayNameTest : DescribeSpec({
    describe("DisplayName") {
        describe("create") {
            it("should create valid display name") {
                val result = DisplayName.create("Valid Display Name")
                result.isRight() shouldBe true
            }
            
            it("should reject blank name") {
                val result = DisplayName.create("   ")
                result.isLeft() shouldBe true
            }
            
            it("should reject too long name") {
                val result = DisplayName.create("a".repeat(61))
                result.isLeft() shouldBe true
            }
        }
    }
})
package org.blackerp.domain.ad.value

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class ModuleNameTest : DescribeSpec({
    describe("ModuleName") {
        describe("create") {
            it("should create valid module name") {
                val result = ModuleName.create("valid-module")
                result.shouldBeRight().also { name ->
                    name.value shouldBe "valid-module"
                }
            }

            it("should reject invalid format") {
                val result = ModuleName.create("Invalid Module")
                result.shouldBeLeft().also { error ->
                    error.message shouldBe "Module name must start with lowercase letter and contain only lowercase letters, numbers, and hyphens"
                }
            }

            it("should enforce length constraints") {
                ModuleName.create("ab").shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("module name", 3, 50)
                }

                ModuleName.create("a".repeat(51)).shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("module name", 3, 50)
                }
            }
        }
    }
})

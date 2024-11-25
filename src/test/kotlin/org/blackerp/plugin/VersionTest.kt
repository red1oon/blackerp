package org.blackerp.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class VersionTest : DescribeSpec({
    describe("Version") {
        describe("create") {
            it("should create valid version") {
                val result = Version.create("1.2.3")
                result.shouldBeRight().also { version ->
                    version.major shouldBe 1
                    version.minor shouldBe 2
                    version.patch shouldBe 3
                }
            }

            it("should reject invalid format") {
                val testCases = listOf(
                    "1" to "Version must be in format major.minor.patch",
                    "1.2" to "Version must be in format major.minor.patch",
                    "1.2.3.4" to "Version must be in format major.minor.patch",
                    "a.b.c" to "Version must be in format major.minor.patch",
                    "1.2.x" to "Version must be in format major.minor.patch",
                    ".1.2" to "Version must be in format major.minor.patch"
                )

                testCases.forEach { (input, expectedMessage) ->
                    Version.create(input).shouldBeLeft().also { error ->
                        error shouldBe ValidationError.InvalidFormat(expectedMessage)
                    }
                }
            }
        }

        describe("comparison") {
            it("should compare versions correctly") {
                val v1 = Version.create("1.0.0").getOrNull()!!
                val v2 = Version.create("2.0.0").getOrNull()!!
                val v3 = Version.create("2.1.0").getOrNull()!!
                val v4 = Version.create("2.1.1").getOrNull()!!

                (v1 < v2) shouldBe true
                (v2 < v3) shouldBe true
                (v3 < v4) shouldBe true
                (v4 > v1) shouldBe true
            }

            it("should handle equal versions") {
                val v1 = Version.create("1.0.0").getOrNull()!!
                val v2 = Version.create("1.0.0").getOrNull()!!

                (v1 == v2) shouldBe true
                (v1 >= v2) shouldBe true
                (v1 <= v2) shouldBe true
            }
        }
    }
})
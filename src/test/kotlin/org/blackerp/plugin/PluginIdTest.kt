package org.blackerp.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class PluginIdTest : DescribeSpec({
    describe("PluginId") {
        describe("create") {
            it("should create valid plugin id") {
                val result = PluginId.create("valid-plugin")
                result.shouldBeRight().also { id ->
                    id.value shouldBe "valid-plugin"
                }
            }

            it("should reject invalid format") {
                val invalidFormats = listOf(
                    "Invalid Plugin",  // Contains spaces
                    "1invalid",        // Starts with number
                    "INVALID",         // Contains uppercase
                    "invalid!plugin"   // Contains special characters
                )

                invalidFormats.forEach { invalid ->
                    val result = PluginId.create(invalid)
                    result.shouldBeLeft().also { error ->
                        error shouldBe ValidationError.InvalidFormat(
                            "Plugin ID must start with lowercase letter and contain only lowercase letters, numbers, and hyphens"
                        )
                    }
                }
            }

            it("should enforce length constraints") {
                // Too short
                PluginId.create("ab").shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("plugin id", 3, 50)
                }

                // Too long
                PluginId.create("a".repeat(51)).shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("plugin id", 3, 50)
                }
            }
        }
    }
})
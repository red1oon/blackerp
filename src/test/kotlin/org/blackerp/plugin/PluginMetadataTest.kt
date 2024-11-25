package org.blackerp.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeLeft
import org.blackerp.shared.ValidationError

class PluginMetadataTest : DescribeSpec({
    describe("PluginMetadata") {
        describe("create") {
            val validId = PluginId.create("test-plugin").getOrNull()!!
            val validVersion = Version.create("1.0.0").getOrNull()!!

            it("should create valid metadata") {
                val result = PluginMetadata.create(
                    id = validId,
                    version = validVersion,
                    name = "Test Plugin",
                    description = "A test plugin",
                    vendor = "Test Vendor"
                )

                result.shouldBeRight().also { metadata ->
                    metadata.id shouldBe validId
                    metadata.version shouldBe validVersion
                    metadata.name shouldBe "Test Plugin"
                    metadata.description shouldBe "A test plugin"
                    metadata.vendor shouldBe "Test Vendor"
                }
            }

            it("should validate name") {
                val result = PluginMetadata.create(
                    id = validId,
                    version = validVersion,
                    name = "",
                    description = "A test plugin",
                    vendor = "Test Vendor"
                )

                result.shouldBeLeft().also { error ->
                    error shouldBe ValidationError.Required("name")
                }
            }

            it("should validate description length") {
                val result = PluginMetadata.create(
                    id = validId,
                    version = validVersion,
                    name = "Test Plugin",
                    description = "a".repeat(501),
                    vendor = "Test Vendor"
                )

                result.shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("description", 0, 500)
                }
            }

            it("should validate vendor") {
                // Empty vendor
                PluginMetadata.create(
                    id = validId,
                    version = validVersion,
                    name = "Test Plugin",
                    description = "A test plugin",
                    vendor = ""
                ).shouldBeLeft().also { error ->
                    error shouldBe ValidationError.Required("vendor")
                }

                // Too long vendor
                PluginMetadata.create(
                    id = validId,
                    version = validVersion,
                    name = "Test Plugin",
                    description = "A test plugin",
                    vendor = "a".repeat(101)
                ).shouldBeLeft().also { error ->
                    error shouldBe ValidationError.InvalidLength("vendor", 3, 100)
                }
            }
        }
    }
})
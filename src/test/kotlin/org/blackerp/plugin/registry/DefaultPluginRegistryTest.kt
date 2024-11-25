package org.blackerp.plugin.registry

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.blackerp.plugin.*

class DefaultPluginRegistryTest : DescribeSpec({
    describe("DefaultPluginRegistry") {
        val registry = DefaultPluginRegistry()
        val pluginId = PluginId.create("test-plugin").getOrNull()!!
        val version = Version.create("1.0.0").getOrNull()!!
        val metadata = PluginMetadata.create(
            id = pluginId,
            version = version,
            name = "Test Plugin",
            description = "Test plugin description",
            vendor = "Test Vendor"
        ).getOrNull()!!
        
        val plugin = TestPlugin(metadata)

        it("should register plugin") {
            runTest {
                registry.register(plugin).isRight() shouldBe true
                registry.getPlugins().size shouldBe 1
            }
        }

        it("should prevent duplicate registration") {
            runTest {
                registry.register(plugin)
                registry.register(plugin).isLeft() shouldBe true
            }
        }

        it("should retrieve registered plugin") {
            runTest {
                registry.register(plugin)
                registry.getPlugin(pluginId).isRight() shouldBe true
            }
        }

        it("should unregister plugin") {
            runTest {
                registry.register(plugin)
                registry.unregister(pluginId).isRight() shouldBe true
                registry.getPlugins().isEmpty() shouldBe true
            }
        }
    }
})

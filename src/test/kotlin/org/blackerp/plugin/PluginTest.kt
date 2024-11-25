// Classpath: src/test/kotlin/org/blackerp/plugin/PluginTest.kt
package org.blackerp.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest

class PluginTest : DescribeSpec({
    describe("Plugin") {
        val validId = PluginId.create("test-plugin").getOrNull()!!
        val validVersion = Version.create("1.0.0").getOrNull()!!
        val validMetadata = PluginMetadata.create(
            id = validId,
            version = validVersion,
            name = "Test Plugin",
            description = "A test plugin",
            vendor = "Test Vendor"
        ).getOrNull()!!
        
        lateinit var plugin: TestPlugin
        
        beforeTest {
            plugin = TestPlugin(validMetadata)
        }
        
        describe("lifecycle") {
            it("should handle initialization") {
                runTest {
                    val result = plugin.initialize()
                    result.isRight() shouldBe true
                    plugin.initialized shouldBe true
                }
            }
            
            it("should handle shutdown") {
                runTest {
                    val result = plugin.shutdown()
                    result.isRight() shouldBe true
                    plugin.shutdown shouldBe true
                }
            }
        }
        
        describe("extension registration") {
            it("should register extensions") {
                runTest {
                    val registry = InMemoryExtensionRegistry()
                    val result = plugin.registerExtensions(registry)
                    result.isRight() shouldBe true
                }
            }
        }
    }
})
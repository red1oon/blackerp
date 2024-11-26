package org.blackerp.integration.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.arrow.core.shouldBeRight
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.blackerp.integration.IntegrationTestConfig
import org.springframework.context.annotation.Import
import org.blackerp.plugin.*
import org.blackerp.plugin.registry.DefaultPluginRegistry
import org.blackerp.plugin.registry.PluginRegistry

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig::class)
class PluginLifecycleIntegrationTest : DescribeSpec({
    
    val pluginRegistry: PluginRegistry = DefaultPluginRegistry()
    
    describe("Plugin Lifecycle") {
        it("should load and initialize plugin") {
            // given
            val pluginId = PluginId.create("test-plugin").getOrNull()!!
            val version = Version.create("1.0.0").getOrNull()!!
            val metadata = PluginMetadata.create(
                id = pluginId,
                version = version,
                name = "Test Plugin",
                description = "Test plugin",
                vendor = "Test Vendor"
            ).getOrNull()!!
            
            val plugin = TestPlugin(metadata)
            
            // when
            val registerResult = pluginRegistry.register(plugin)
            
            // then
            registerResult.shouldBeRight()
        }
    }
})

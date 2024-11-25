#!/bin/bash

BASE_DIR="$(pwd)"
TEST_DIR="$BASE_DIR/src/test/kotlin/org/blackerp"

# Integration test directory structure
INTEGRATION_DIR="$TEST_DIR/integration"

# First clean up previously created files
echo "Cleaning up previous integration test files..."
rm -rf "$INTEGRATION_DIR"

# Recreate directory structure
mkdir -p "$INTEGRATION_DIR/api"
mkdir -p "$INTEGRATION_DIR/db"
mkdir -p "$INTEGRATION_DIR/plugin"

echo "Creating new integration test files..."

# Integration test configuration
cat > "$INTEGRATION_DIR/IntegrationTestConfig.kt" << 'EOF'
package org.blackerp.integration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

@TestConfiguration
class IntegrationTestConfig {
    @Bean
    fun dataSource(): DataSource =
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("db/h2-schema.sql")
            .build()

    @Bean
    fun testRestTemplate() = TestRestTemplate()
}
EOF

# API Integration Tests
cat > "$INTEGRATION_DIR/api/TableApiIntegrationTest.kt" << 'EOF'
package org.blackerp.integration.api

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.blackerp.integration.IntegrationTestConfig
import org.springframework.context.annotation.Import
import org.blackerp.api.dto.CreateTableRequest
import org.blackerp.shared.TestFactory

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestConfig::class)
class TableApiIntegrationTest(
    private val restTemplate: TestRestTemplate,
    @LocalServerPort private val port: Int
) : DescribeSpec({
    
    describe("Table API") {
        context("POST /api/tables") {
            it("should create table successfully") {
                // given
                val request = TestFactory.createTableRequest()
                
                // when
                val response = restTemplate.postForEntity(
                    "http://localhost:$port/api/tables",
                    request,
                    Any::class.java
                )
                
                // then
                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})
EOF

# Database Integration Tests
cat > "$INTEGRATION_DIR/db/TableRepositoryIntegrationTest.kt" << 'EOF'
package org.blackerp.integration.db

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.arrow.core.shouldBeRight
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.blackerp.integration.IntegrationTestConfig
import org.springframework.context.annotation.Import
import org.blackerp.infrastructure.persistence.store.PostgresTableOperations
import org.blackerp.shared.TestFactory
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig::class)
class TableRepositoryIntegrationTest(
    private val jdbcTemplate: JdbcTemplate
) : DescribeSpec({
    
    lateinit var tableOperations: PostgresTableOperations
    
    beforeTest {
        tableOperations = PostgresTableOperations(jdbcTemplate)
    }
    
    describe("TableRepository") {
        it("should save and retrieve table") {
            // given
            val table = TestFactory.createTestTable()
            
            // when
            val saveResult = tableOperations.save(table)
            val findResult = tableOperations.findById(table.metadata.id)
            
            // then
            saveResult.shouldBeRight()
            findResult.shouldBeRight()
        }
    }
})
EOF

# Plugin Integration Tests
cat > "$INTEGRATION_DIR/plugin/PluginLifecycleIntegrationTest.kt" << 'EOF'
package org.blackerp.integration.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
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
    val extensionRegistry: ExtensionRegistry = InMemoryExtensionRegistry()
    
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
EOF

echo "Recreated integration test files:"
echo "1. $INTEGRATION_DIR/IntegrationTestConfig.kt"
echo "2. $INTEGRATION_DIR/api/TableApiIntegrationTest.kt"
echo "3. $INTEGRATION_DIR/db/TableRepositoryIntegrationTest.kt"
echo "4. $INTEGRATION_DIR/plugin/PluginLifecycleIntegrationTest.kt"
echo ""
echo "Next steps:"
echo "1. Run './gradlew test' to verify integration tests"
EOF

chmod +x recreate_integration_tests.sh

This script will:
1. Clean up any previously created files
2. Create fresh integration test structure
3. Use existing test configuration from src/test/resources/application.yml

Would you like me to:
1. Make any adjustments to the test implementations?
2. Proceed with the roadmap updates?
3. Focus on something else?
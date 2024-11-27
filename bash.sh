#!/bin/bash

# setup-backend-events.sh
# Run from project root directory
# Sets up event infrastructure for backend

echo "Setting up backend event infrastructure..."

# Create necessary directories
mkdir -p src/main/kotlin/org/blackerp/infrastructure/event
mkdir -p src/main/kotlin/org/blackerp/config

# Create EventPublisher implementation
cat > src/main/kotlin/org/blackerp/infrastructure/event/DefaultEventPublisher.kt << 'EOL'
package org.blackerp.infrastructure.event

import arrow.core.Either
import arrow.core.right
import org.blackerp.domain.event.DomainEvent
import org.blackerp.domain.table.TableError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultEventPublisher : EventPublisher {
    private val logger = LoggerFactory.getLogger(DefaultEventPublisher::class.java)

    override suspend fun publish(event: DomainEvent): Either<TableError, Unit> {
        logger.info("Publishing event: ${event::class.simpleName} with ID: ${event.metadata.id}")
        // In a real implementation, you might:
        // 1. Persist the event
        // 2. Send to message broker
        // 3. Notify subscribers
        return Unit.right()
    }
}
EOL

# Create Spring configuration
cat > src/main/kotlin/org/blackerp/config/EventConfig.kt << 'EOL'
package org.blackerp.config

import org.blackerp.infrastructure.event.DefaultEventPublisher
import org.blackerp.infrastructure.event.EventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventConfig {
    @Bean
    fun eventPublisher(): EventPublisher = DefaultEventPublisher()
}
EOL

# Update Application class with component scanning
cat > src/main/kotlin/org/blackerp/BlackErpApplication.kt << 'EOL'
package org.blackerp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["org.blackerp"]
)
class BlackErpApplication

fun main(args: Array<String>) {
    runApplication<BlackErpApplication>(*args)
}
EOL

# Create test configuration
mkdir -p src/test/kotlin/org/blackerp/config

cat > src/test/kotlin/org/blackerp/config/TestEventConfig.kt << 'EOL'
package org.blackerp.config

import io.mockk.mockk
import org.blackerp.infrastructure.event.EventPublisher
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestEventConfig {
    @Bean
    @Primary
    fun testEventPublisher(): EventPublisher = mockk(relaxed = true)
}
EOL

echo "Setup complete! Make sure you have the following dependencies in your build.gradle.kts:"
echo "1. implementation(\"org.springframework.boot:spring-boot-starter\")"
echo "2. implementation(\"io.arrow-kt:arrow-core:1.2.0\")"
echo "3. testImplementation(\"io.mockk:mockk:1.13.8\")"

chmod +x setup-backend-events.sh

This script:
1. Creates the `DefaultEventPublisher` implementation
2. Sets up Spring configuration for event publishing
3. Updates the main application class with proper component scanning
4. Creates test configuration for mocking events in tests

To use:
1. Save as `setup-backend-events.sh`
2. Make executable: `chmod +x setup-backend-events.sh`
3. Run: `./setup-backend-events.sh`

The key components are:

1. `DefaultEventPublisher`: A basic implementation that logs events (can be enhanced later)
2. `EventConfig`: Spring configuration to wire up the event publisher
3. `TestEventConfig`: Test configuration using mockk for testing
4. Updated application class with proper component scanning

Would you like me to:
1. Add more features to the event publisher (like async handling)?
2. Create test cases for the event infrastructure?
3. Help with something else?
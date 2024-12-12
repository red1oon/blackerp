// File: integration-tests/test/kotlin/org/blackerp/test/config/TestConfig.kt
package org.blackerp.test.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.blackerp.domain.core.ad.metadata.services.MetadataService
import org.blackerp.domain.core.ad.metadata.services.MetadataError
import org.blackerp.domain.core.ad.metadata.services.RuleEvaluator
import org.blackerp.domain.core.ad.metadata.entities.*
import org.blackerp.domain.core.ad.metadata.expression.*
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.User
import arrow.core.Either
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.blackerp.domain.core.shared.ValidationError
import java.util.UUID

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = ["org.blackerp"])
class TestConfig {
    @Bean
    fun metadataService(): MetadataService = object : MetadataService {
        override suspend fun getRules(entityType: String): Flow<ADRule> = flow { }
        override suspend fun getRule(id: UUID): Either<MetadataError, ADRule?> = null.right()
        override suspend fun getValidations(entityType: String): Flow<ADValidationRule> = flow { }
        override suspend fun getStatusFlow(documentType: String): Flow<ADStatusLine> = flow { }
    }

    @Bean
    fun ruleEvaluator(): RuleEvaluator = object : RuleEvaluator {
        override suspend fun evaluate(rule: ADRule, context: Map<String, Any>): Either<ValidationError, Boolean> =
            true.right()

        override suspend fun validateParameters(rule: ADRule, parameters: Map<String, Any>): Either<ValidationError, Map<String, Any>> =
            parameters.right()
    }

    @Bean
    fun securityContext() = SecurityContext(
        user = User(
            id = UUID.randomUUID(),
            username = "test-user",
            email = "test@example.com",
            clientId = UUID.randomUUID(),
            organizationId = null,
            roles = emptySet()
        ),
        clientId = UUID.randomUUID(),
        organizationId = null,
        roles = emptySet()
    )
}
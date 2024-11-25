// File: src/test/kotlin/org/blackerp/config/TestConfig.kt
package org.blackerp.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource
import com.fasterxml.jackson.databind.ObjectMapper
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.api.mappers.TableMapper
import org.blackerp.infrastructure.event.EventPublisher
import org.blackerp.infrastructure.persistence.store.InMemoryTableOperations
import io.mockk.mockk

@TestConfiguration
class TestConfig {
    
    @Bean
    @Primary
    fun dataSource(): DataSource =
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build()

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate =
        JdbcTemplate(dataSource)
    
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
    
    @Bean
    fun tableMapper(): TableMapper = TableMapper()
    
    @Bean
    fun eventPublisher(): EventPublisher = mockk(relaxed = true)
    
    @Bean
    fun createTableUseCase(
        eventPublisher: EventPublisher
    ): CreateTableUseCase = CreateTableUseCase(
        operations = InMemoryTableOperations(),
        eventPublisher = eventPublisher
    )
}

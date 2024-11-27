package org.blackerp.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.domain.table.TableOperations
import org.blackerp.infrastructure.event.EventPublisher
import org.blackerp.infrastructure.persistence.store.*
import org.springframework.boot.test.web.client.TestRestTemplate
import io.mockk.mockk
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy

@TestConfiguration
class UnifiedTestConfig {
    
    @Bean
    @Primary
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("db/h2-schema.sql")
            .build()
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = 
        JdbcTemplate(dataSource)

    @Bean
    fun testRestTemplate(): TestRestTemplate = 
        TestRestTemplate()

    @Bean
    fun eventPublisher(): EventPublisher = 
        mockk(relaxed = true)

    @Bean
    @Primary
    fun tableOperations(jdbcTemplate: JdbcTemplate): TableOperations =
        PostgresTableOperations(jdbcTemplate)

    @Bean
    fun postgresTabOperations(
        jdbcTemplate: JdbcTemplate,
        tableOperations: TableOperations
    ): PostgresTabOperations =
        PostgresTabOperations(jdbcTemplate, tableOperations)

    @Bean
    fun createTableUseCase(
        tableOperations: TableOperations,
        eventPublisher: EventPublisher
    ): CreateTableUseCase =
        CreateTableUseCase(tableOperations, eventPublisher)

    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy =
        FlywayMigrationStrategy { _ -> /* disable automatic migrations */ }
}
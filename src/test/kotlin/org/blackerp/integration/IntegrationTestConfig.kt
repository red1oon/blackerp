package org.blackerp.integration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import javax.sql.DataSource
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.jdbc.core.JdbcTemplate
import org.blackerp.application.table.CreateTableUseCase
import org.blackerp.infrastructure.event.EventPublisher
import org.blackerp.infrastructure.persistence.store.PostgresTableOperations
import io.mockk.mockk

@TestConfiguration
class IntegrationTestConfig {
    @Bean
    @Primary
    fun dataSource(): DataSource =
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("db/h2-schema.sql")
            .build()

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = 
        JdbcTemplate(dataSource)

    @Bean
    fun testRestTemplate() = TestRestTemplate()

    @Bean
    fun eventPublisher(): EventPublisher = mockk(relaxed = true)

    @Bean
    fun tableOperations(jdbcTemplate: JdbcTemplate): PostgresTableOperations =
        PostgresTableOperations(jdbcTemplate)

    @Bean
    fun createTableUseCase(
        tableOperations: PostgresTableOperations,
        eventPublisher: EventPublisher
    ): CreateTableUseCase = CreateTableUseCase(tableOperations, eventPublisher)
}
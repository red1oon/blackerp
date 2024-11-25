// File: src/test/kotlin/org/blackerp/infrastructure/persistence/TestDatabaseConfig.kt
package org.blackerp.infrastructure.persistence

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@TestConfiguration
class TestDatabaseConfig {
    
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
}
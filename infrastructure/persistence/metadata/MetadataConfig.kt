package org.blackerp.infrastructure.persistence.metadata

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class MetadataConfig {
    @Bean 
    fun jdbcTemplate(dataSource: DataSource) = JdbcTemplate(dataSource)
}

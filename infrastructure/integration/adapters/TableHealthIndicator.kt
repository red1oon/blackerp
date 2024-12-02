package org.blackerp.infrastructure.integration.adapters

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.springframework.jdbc.core.JdbcTemplate
import org.slf4j.LoggerFactory

@Component
class TableHealthIndicator(
    private val jdbcTemplate: JdbcTemplate
) : HealthIndicator {
    private val logger = LoggerFactory.getLogger(TableHealthIndicator::class.java)

    override fun health(): Health {
        return try {
            val tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ad_table",
                Int::class.java
            ) ?: 0

            val recentTablesCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM ad_table 
                WHERE created > now() - interval '24 hours'
            """, Int::class.java) ?: 0

            Health.up()
                .withDetail("totalTables", tableCount)
                .withDetail("tablesCreatedLast24h", recentTablesCount)
                .build()
        } catch (e: Exception) {
            logger.error("Health check failed", e)
            Health.down()
                .withException(e)
                .build()
        }
    }
}

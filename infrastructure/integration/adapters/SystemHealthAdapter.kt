package org.blackerp.infrastructure.integration.adapters

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.springframework.jdbc.core.JdbcTemplate

@Component
class SystemHealthAdapter(
    private val jdbcTemplate: JdbcTemplate
) : HealthIndicator {
    
    override fun health(): Health {
        return try {
            val dbStatus = checkDatabase()
            if (dbStatus) {
                Health.up()
                    .withDetail("database", "UP")
                    .build()
            } else {
                Health.down()
                    .withDetail("database", "DOWN")
                    .build()
            }
        } catch (ex: Exception) {
            Health.down()
                .withException(ex)
                .build()
        }
    }

    private fun checkDatabase(): Boolean =
        try {
            jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            true
        } catch (e: Exception) {
            false
        }
}

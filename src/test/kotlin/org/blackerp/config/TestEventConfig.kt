package org.blackerp.config

import org.springframework.test.context.ActiveProfiles
import io.mockk.mockk
import org.blackerp.infrastructure.event.EventPublisher
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
 
@ActiveProfiles("test")
class TestEventConfig {
    @Bean
    @Primary
    fun testEventPublisher(): EventPublisher = mockk(relaxed = true)
}

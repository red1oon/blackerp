package org.blackerp.config

import org.blackerp.infrastructure.event.DefaultEventPublisher
import org.blackerp.infrastructure.event.EventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class EventConfig {
    @Bean
    fun eventPublisher(): EventPublisher = DefaultEventPublisher()
}

package org.blackerp.infrastructure.events.publishers

import org.springframework.stereotype.Component
import org.springframework.context.ApplicationEventPublisher
import org.blackerp.domain.events.DomainEvent
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

@Component
class DomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val logger = LoggerFactory.getLogger(DomainEventPublisher::class.java)

    fun publish(event: DomainEvent) {
        val correlationId = MDC.get("correlationId") ?: UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)
        
        try {
            logger.debug("Publishing event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId)
            
            applicationEventPublisher.publishEvent(event)
            
            logger.debug("Successfully published event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId)
        } catch (e: Exception) {
            logger.error("Failed to publish event: {} with correlationId: {}", 
                event.javaClass.simpleName, correlationId, e)
            throw e
        } finally {
            MDC.remove("correlationId")
        }
    }
}

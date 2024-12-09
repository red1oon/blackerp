package org.blackerp.infrastructure.events.handlers

import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener
import org.blackerp.domain.events.WindowEvent
import org.blackerp.application.services.cache.WindowCacheStrategy
import org.slf4j.LoggerFactory

@Component
class WindowEventHandler(
    private val windowCache: WindowCacheStrategy
) {
    private val logger = LoggerFactory.getLogger(WindowEventHandler::class.java)

    @EventListener
    suspend fun onWindowCreated(event: WindowEvent.WindowCreated) {
        logger.info("Window created: ${event.name} (${event.windowId})")
        windowCache.evictAll()
    }

    @EventListener
    suspend fun onWindowUpdated(event: WindowEvent.WindowUpdated) {
        logger.info("Window updated: ${event.windowId}")
        windowCache.evictAll()
    }

    @EventListener
    suspend fun onWindowDeleted(event: WindowEvent.WindowDeleted) {
        logger.info("Window deleted: ${event.windowId}")
        windowCache.evictAll()
    }

    @EventListener
    suspend fun onTabAdded(event: WindowEvent.TabAdded) {
        logger.info("Tab added to window ${event.windowId}: ${event.name}")
        windowCache.evictAll()
    }

    @EventListener
    suspend fun onTabRemoved(event: WindowEvent.TabRemoved) {
        logger.info("Tab removed from window ${event.windowId}")
        windowCache.evictAll()
    }
}

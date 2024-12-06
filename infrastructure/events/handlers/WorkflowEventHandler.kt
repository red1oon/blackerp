package org.blackerp.infrastructure.events.handlers

import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener
import org.blackerp.domain.events.WorkflowEvent
import org.slf4j.LoggerFactory

@Component
class WorkflowEventHandler {
    private val logger = LoggerFactory.getLogger(WorkflowEventHandler::class.java)

    @EventListener
    fun handleNodeCreated(event: WorkflowEvent.NodeCreated) {
        logger.info("Node created: {} (type: {})", 
            event.node.displayName,
            event.node.type)
    }

    @EventListener
    fun handleNodeUpdated(event: WorkflowEvent.NodeUpdated) {
        logger.info("Node updated: {} with changes: {}", 
            event.nodeId,
            event.changes)
    }

    @EventListener
    fun handleNodeDeleted(event: WorkflowEvent.NodeDeleted) {
        logger.info("Node deleted: {} ({})", 
            event.nodeId,
            event.nodeName)
    }
}

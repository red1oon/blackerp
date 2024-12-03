package org.blackerp.domain.events

import java.time.Instant
import java.util.UUID
//Skipping: No correct import specified for java.time.Instant in ./domain/events/DomainEvent.kt
interface DomainEvent {
   val metadata: EventMetadata
   val entityId: UUID
   val entityType: String
   val eventType: String
   val changes: Map<String, ChangePair>
}

data class EventMetadata(
   val id: UUID = UUID.randomUUID(),
   val timestamp: Instant = Instant.now(),
   val user: String,
   val version: Int,
   val correlationId: String? = null
)

data class ChangePair(
   val oldValue: Any?,
   val newValue: Any?
) {
   val hasChanged: Boolean get() = oldValue != newValue
}

// Extension function to create audit event easily
fun <T : Any> T.toAuditEvent(
   entityId: UUID,
   eventType: String,
   user: String,
   version: Int,
   oldValues: Map<String, Any?> = emptyMap()
): DomainEvent {
   return object : DomainEvent {
       override val metadata = EventMetadata(user = user, version = version)
       override val entityId = entityId
       override val entityType = this@toAuditEvent.javaClass.simpleName
       override val eventType = eventType
       override val changes = oldValues.mapValues { (key, oldValue) ->
           ChangePair(oldValue, this@toAuditEvent.getProperty(key))
       }
   }
}

private fun Any.getProperty(propertyName: String): Any? = 
   this.javaClass.kotlin.members
       .find { it.name == propertyName }
       ?.call(this)

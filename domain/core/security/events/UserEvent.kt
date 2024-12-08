package org.blackerp.domain.core.security.events

import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.domain.core.security.User

sealed class UserEvent : DomainEvent {
    data class UserCreated(
        override val metadata: EventMetadata,
        val user: User
    ) : UserEvent()

    data class UserUpdated(
        override val metadata: EventMetadata,
        val user: User
    ) : UserEvent()

    data class UserLoginAttempted(
        override val metadata: EventMetadata,
        val username: String,
        val success: Boolean,
        val failureReason: String?
    ) : UserEvent()
}

package org.blackerp.domain.events

import org.blackerp.domain.core.security.User

sealed class UserEvent {
    data class UserCreated(val user: User) : UserEvent()
    data class UserUpdated(val user: User) : UserEvent()
    data class UserLoginAttempted(
        val username: String,
        val success: Boolean,
        val failureReason: String?
    ) : UserEvent()
}

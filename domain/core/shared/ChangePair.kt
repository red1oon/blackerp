package org.blackerp.domain.core.shared

data class ChangePair<T>(
    val oldValue: T,
    val newValue: T
) {
    val hasChanged: Boolean = oldValue != newValue
}

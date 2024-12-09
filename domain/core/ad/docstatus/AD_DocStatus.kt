package org.blackerp.domain.core.ad.docstatus

import arrow.core.Either
import arrow.core.right
import arrow.core.left

/** In-memory document status registry for POC */
object AD_DocStatus {
    // Standard document statuses
    const val DRAFT = "DR"
    const val IN_PROGRESS = "IP"
    const val COMPLETED = "CO"
    const val VOIDED = "VO"
    const val CLOSED = "CL"

    // Status display names
    private val displayNames = mapOf(
        DRAFT to "Draft",
        IN_PROGRESS to "In Progress",
        COMPLETED to "Completed",
        VOIDED to "Voided",
        CLOSED to "Closed"
    )

    // Allowed transitions per status
    private val transitions = mapOf(
        DRAFT to listOf(IN_PROGRESS, VOIDED),
        IN_PROGRESS to listOf(COMPLETED, VOIDED),
        COMPLETED to listOf(CLOSED, VOIDED),
        VOIDED to emptyList<String>(),
        CLOSED to emptyList<String>()
    )

    fun getDisplayName(status: String): String = displayNames[status] ?: status

    fun validateTransition(fromStatus: String, toStatus: String): Either<DocStatusError, Unit> {
        if (!transitions.containsKey(fromStatus)) {
            return DocStatusError.InvalidStatus(fromStatus).left()
        }
        if (!transitions.containsKey(toStatus)) {
            return DocStatusError.InvalidStatus(toStatus).left()
        }
        return if (canTransition(fromStatus, toStatus)) {
            Unit.right()
        } else {
            DocStatusError.StatusTransitionInvalid(fromStatus, toStatus).left()
        }
    }

    private fun canTransition(fromStatus: String, toStatus: String): Boolean =
        transitions[fromStatus]?.contains(toStatus) ?: false
}

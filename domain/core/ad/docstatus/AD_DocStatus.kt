package org.blackerp.domain.core.ad.docstatus

import org.slf4j.LoggerFactory
import arrow.core.Either
import arrow.core.right
import arrow.core.left

/**
 * In-memory document status registry for POC
 * Handles static document status definitions and transition rules
 */
object AD_DocStatus {
    private val logger = LoggerFactory.getLogger(AD_DocStatus::class.java)

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

    // Required actions for transitions
    private val requiredActions = mapOf(
        "$DRAFT->$IN_PROGRESS" to "START",
        "$IN_PROGRESS->$COMPLETED" to "COMPLETE",
        "$COMPLETED->$CLOSED" to "CLOSE",
        "*->$VOIDED" to "VOID"
    )

    fun getDisplayName(status: String): String =
        displayNames[status] ?: status

    fun validateTransition(fromStatus: String, toStatus: String): Either<DocStatusError, Unit> {
        logger.debug("Validating transition from $fromStatus to $toStatus")
        
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

    fun getRequiredAction(fromStatus: String, toStatus: String): String? {
        val specificKey = "$fromStatus->$toStatus"
        val wildcardKey = "*->$toStatus"
        
        return requiredActions[specificKey] ?: requiredActions[wildcardKey]
    }

    fun getAllowedNextStatuses(currentStatus: String): List<String> =
        transitions[currentStatus] ?: emptyList()

    private fun canTransition(fromStatus: String, toStatus: String): Boolean =
        transitions[fromStatus]?.contains(toStatus) ?: false
}

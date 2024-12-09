package org.blackerp.infrastructure.workflow.persistence

import org.blackerp.domain.core.workflow.transition.StateTransition
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import java.util.UUID
import org.slf4j.LoggerFactory

@Repository
class TransitionRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(TransitionRepository::class.java)

    // In-memory storage for POC
    private val transitions = mutableMapOf<String, List<StateTransition>>()

    fun findTransitions(fromState: String): List<StateTransition> =
        transitions[fromState] ?: emptyList()

    fun saveTransition(transition: StateTransition): Either<RepositoryError, StateTransition> {
        try {
            val currentTransitions = transitions[transition.fromState] ?: emptyList()
            transitions[transition.fromState] = currentTransitions + transition
            return transition.right()
        } catch (e: Exception) {
            logger.error("Failed to save transition", e)
            return RepositoryError.SaveFailed(e.message ?: "Unknown error").left()
        }
    }
}

sealed class RepositoryError {
    data class SaveFailed(val message: String) : RepositoryError()
    data class NotFound(val id: UUID) : RepositoryError()
}

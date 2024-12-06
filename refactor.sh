# full_security_workflow_audit.sh
#!/bin/bash

# Create workflow state machine
mkdir -p domain/core/workflow/state
mkdir -p domain/core/audit
mkdir -p infrastructure/audit
mkdir -p application/services/workflow
mkdir -p application/services/audit

# 1. Workflow State Machine
cat > domain/core/workflow/state/WorkflowStateMachine.kt << 'EOF'
package org.blackerp.domain.core.workflow.state

import org.blackerp.domain.core.security.SecurityContext
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID

sealed class StateTransitionError {
    data class InvalidTransition(val message: String) : StateTransitionError()
    data class PermissionDenied(val message: String) : StateTransitionError()
    data class ValidationFailed(val message: String) : StateTransitionError()
}

data class WorkflowState(
    val id: UUID,
    val name: String,
    val allowedTransitions: Set<String>,
    val requiredPermissions: Set<String>,
    val validators: List<StateValidator>
)

interface StateValidator {
    suspend fun validate(
        context: SecurityContext,
        currentState: WorkflowState,
        targetState: WorkflowState,
        documentId: UUID
    ): Either<StateTransitionError, Unit>
}

class WorkflowStateMachine(
    private val states: Map<String, WorkflowState>,
    private val initialState: String
) {
    suspend fun transition(
        context: SecurityContext,
        currentStateName: String,
        targetStateName: String,
        documentId: UUID
    ): Either<StateTransitionError, WorkflowState> {
        val currentState = states[currentStateName] ?: return StateTransitionError.InvalidTransition(
            "Invalid current state: $currentStateName"
        ).left()

        val targetState = states[targetStateName] ?: return StateTransitionError.InvalidTransition(
            "Invalid target state: $targetStateName"
        ).left()

        // Check if transition is allowed
        if (!currentState.allowedTransitions.contains(targetStateName)) {
            return StateTransitionError.InvalidTransition(
                "Transition from $currentStateName to $targetStateName not allowed"
            ).left()
        }

        // Check permissions
        if (!hasRequiredPermissions(context, targetState.requiredPermissions)) {
            return StateTransitionError.PermissionDenied(
                "Missing required permissions for state $targetStateName"
            ).left()
        }

        // Run validators
        currentState.validators.forEach { validator ->
            validator.validate(context, currentState, targetState, documentId).fold(
                { error -> return error.left() },
                { /* continue validation */ }
            )
        }

        return targetState.right()
    }

    private fun hasRequiredPermissions(
        context: SecurityContext,
        requiredPermissions: Set<String>
    ): Boolean =
        requiredPermissions.all { permission -> context.hasPermission(permission) }
}
EOF

# 2. Document Access Control
cat > domain/core/security/DocumentAccessControl.kt << 'EOF'
package org.blackerp.domain.core.security

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.UUID
import org.springframework.stereotype.Component

sealed class AccessControlError {
    data class PermissionDenied(val message: String) : AccessControlError()
    data class ValidationFailed(val message: String) : AccessControlError()
}

@Component
class DocumentAccessControl {
    suspend fun checkAccess(
        context: SecurityContext,
        documentId: UUID,
        requiredPermission: String
    ): Either<AccessControlError, Unit> {
        // Check basic permission
        if (!context.hasPermission(requiredPermission)) {
            return AccessControlError.PermissionDenied(
                "Missing required permission: $requiredPermission"
            ).left()
        }

        // TODO: Add additional access control rules based on:
        // - Document ownership
        // - Organization hierarchy
        // - Document type specific rules
        
        return Unit.right()
    }

    suspend fun checkBulkAccess(
        context: SecurityContext,
        documentIds: List<UUID>,
        requiredPermission: String
    ): Map<UUID, Either<AccessControlError, Unit>> =
        documentIds.associateWith { documentId ->
            checkAccess(context, documentId, requiredPermission)
        }
}
EOF

# 3. Audit System
cat > domain/core/audit/AuditLog.kt << 'EOF'
package org.blackerp.domain.core.audit

import java.time.Instant
import java.util.UUID

data class AuditLog(
    val id: UUID = UUID.randomUUID(),
    val timestamp: Instant = Instant.now(),
    val entityType: String,
    val entityId: UUID,
    val action: String,
    val userId: UUID,
    val username: String,
    val clientId: UUID,
    val organizationId: UUID?,
    val oldValue: String?,
    val newValue: String?,
    val metadata: Map<String, String> = emptyMap()
)

interface AuditLogger {
    suspend fun log(entry: AuditLog)
    suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): List<AuditLog>
}
EOF

# Audit Implementation
cat > infrastructure/audit/AuditLoggerImpl.kt << 'EOF'
package org.blackerp.infrastructure.audit

import org.springframework.stereotype.Component
import org.blackerp.domain.core.audit.AuditLog
import org.blackerp.domain.core.audit.AuditLogger
import java.time.Instant
import java.util.UUID
import org.slf4j.LoggerFactory

@Component
class AuditLoggerImpl : AuditLogger {
    private val logger = LoggerFactory.getLogger(AuditLoggerImpl::class.java)
    
    // In-memory storage for POC
    private val auditLogs = mutableListOf<AuditLog>()

    override suspend fun log(entry: AuditLog) {
        auditLogs.add(entry)
        logger.info("Audit log created: ${entry.action} on ${entry.entityType}:${entry.entityId}")
    }

    override suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant?,
        toDate: Instant?
    ): List<AuditLog> =
        auditLogs
            .filter { log ->
                log.entityType == entityType &&
                log.entityId == entityId &&
                (fromDate == null || log.timestamp >= fromDate) &&
                (toDate == null || log.timestamp <= toDate)
            }
            .sortedByDescending { it.timestamp }
}
EOF

# Audit Service
cat > application/services/audit/AuditService.kt << 'EOF'
package org.blackerp.application.services.audit

import org.springframework.stereotype.Service
import org.blackerp.domain.core.audit.AuditLog
import org.blackerp.domain.core.audit.AuditLogger
import org.blackerp.domain.core.security.SecurityContext
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class AuditService(
    private val auditLogger: AuditLogger
) {
    suspend fun logAction(
        context: SecurityContext,
        entityType: String,
        entityId: UUID,
        action: String,
        oldValue: String? = null,
        newValue: String? = null,
        metadata: Map<String, String> = emptyMap()
    ) = withContext(Dispatchers.IO) {
        val auditLog = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = action,
            userId = context.user.id,
            username = context.user.username,
            clientId = context.clientId,
            organizationId = context.organizationId,
            oldValue = oldValue,
            newValue = newValue,
            metadata = metadata
        )
        auditLogger.log(auditLog)
    }

    suspend fun getAuditTrail(
        entityType: String,
        entityId: UUID,
        fromDate: Instant? = null,
        toDate: Instant? = null
    ): List<AuditLog> = withContext(Dispatchers.IO) {
        auditLogger.getAuditTrail(entityType, entityId, fromDate, toDate)
    }
}
EOF

# Workflow Service with Security and Audit
cat > application/services/workflow/WorkflowService.kt << 'EOF'
package org.blackerp.application.services.workflow

import org.springframework.stereotype.Service
import org.blackerp.domain.core.workflow.state.WorkflowStateMachine
import org.blackerp.domain.core.workflow.state.StateTransitionError
import org.blackerp.domain.core.security.SecurityContext
import org.blackerp.domain.core.security.DocumentAccessControl
import org.blackerp.application.services.audit.AuditService
import arrow.core.Either
import arrow.core.flatMap
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class WorkflowService(
    private val stateMachine: WorkflowStateMachine,
    private val accessControl: DocumentAccessControl,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(WorkflowService::class.java)

    suspend fun transitionState(
        context: SecurityContext,
        documentId: UUID,
        currentState: String,
        targetState: String
    ): Either<StateTransitionError, Unit> {
        logger.debug("Attempting state transition for document $documentId: $currentState -> $targetState")

        return accessControl.checkAccess(context, documentId, "WORKFLOW_TRANSITION")
            .mapLeft { error -> 
                StateTransitionError.PermissionDenied(error.toString())
            }
            .flatMap {
                stateMachine.transition(context, currentState, targetState, documentId)
                    .also { result ->
                        result.fold(
                            { error ->
                                logger.error("State transition failed: ${error.message}")
                            },
                            { newState ->
                                auditService.logAction(
                                    context = context,
                                    entityType = "DOCUMENT",
                                    entityId = documentId,
                                    action = "STATE_TRANSITION",
                                    oldValue = currentState,
                                    newValue = targetState,
                                    metadata = mapOf(
                                        "workflow_state" to newState.name,
                                        "transition_type" to "STATE_CHANGE"
                                    )
                                )
                            }
                        )
                    }
            }
            .map { Unit }
    }
}
EOF

echo "Workflow state machine, document access control, and audit system created successfully"

./compile.sh 
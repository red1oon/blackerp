// File: application/services/window/WindowServiceImpl.kt
package org.blackerp.application.services.window

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.ad.window.*
import org.blackerp.domain.events.WindowEvent
import org.blackerp.domain.events.EventMetadata
import org.blackerp.infrastructure.events.publishers.DomainEventPublisher
import org.blackerp.domain.core.values.*
import org.blackerp.domain.core.metadata.*
import arrow.core.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import org.slf4j.LoggerFactory

@Service
class WindowServiceImpl(
    private val windowRepository: WindowRepository,
    private val eventPublisher: DomainEventPublisher
) : WindowService {
    private val logger = LoggerFactory.getLogger(WindowServiceImpl::class.java)

    @Transactional
    override suspend fun createWindow(command: CreateWindowCommand): Either<WindowError, ADWindow> {
        logger.debug("Creating window: ${command.name}")
        
        // Validate window name format
        return WindowName.create(command.name)
            .mapLeft { WindowError.ValidationFailed("Invalid window name: ${it.message}") }
            .flatMap { windowName ->
                // Validate display name
                DisplayName.create(command.displayName)
                    .mapLeft { WindowError.ValidationFailed("Invalid display name: ${it.message}") }
                    .flatMap { displayName ->
                        // Handle optional description
                        val description = command.description?.let { desc ->
                            Description.create(desc)
                                .mapLeft { WindowError.ValidationFailed("Invalid description: ${it.message}") }
                                .getOrNull()
                        }

                        // Create window entity
                        val window = ADWindow(
                            metadata = EntityMetadata(
                                id = UUID.randomUUID().toString(),
                                audit = AuditInfo(
                                    createdBy = "system", // TODO: Get from security context
                                    updatedBy = "system"
                                )
                            ),
                            name = windowName,
                            displayName = displayName,
                            description = description,
                            tabs = command.tabs.mapNotNull { createTab(it).getOrNull() },
                            isActive = command.isActive ?: true,
                            isSOTrx = command.isSOTrx ?: true,
                            windowType = command.windowType ?: WindowType.MAINTAIN
                        )

                        // Save and publish event
                        windowRepository.save(window)
                            .onRight { saved ->
                                publishWindowCreated(saved)
                            }
                    }
            }
    }

    private fun createTab(command: CreateTabCommand): Either<WindowError, ADTab> {
        return TabName.create(command.name)
            .mapLeft { WindowError.ValidationFailed("Invalid tab name: ${it.message}") }
            .flatMap { tabName ->
                DisplayName.create(command.displayName)
                    .mapLeft { WindowError.ValidationFailed("Invalid tab display name: ${it.message}") }
                    .map { displayName ->
                        ADTab(
                            metadata = EntityMetadata(
                                id = UUID.randomUUID().toString(),
                                audit = AuditInfo(
                                    createdBy = "system",
                                    updatedBy = "system"
                                )
                            ),
                            name = tabName,
                            displayName = displayName,
                            description = command.description?.let { 
                                Description.create(it).getOrNull() 
                            },
                            tableId = command.tableId,
                            sequence = command.sequence ?: 10,
                            fields = command.fields.map { createField(it) }
                        )
                    }
            }
    }

    private fun createField(command: CreateFieldCommand): WindowField {
        return WindowField(
            id = UUID.randomUUID(),
            columnName = command.columnName,
            displayName = command.displayName,
            description = command.description,
            isDisplayed = command.isDisplayed ?: true,
            isReadOnly = command.isReadOnly ?: false,
            isMandatory = command.isMandatory ?: false,
            sequence = command.sequence ?: 10,
            displayLogic = command.displayLogic,
            defaultValue = command.defaultValue,
            validationRule = command.validationRule
        )
    }

    @Transactional
    override suspend fun updateWindow(id: UUID, command: UpdateWindowCommand): Either<WindowError, ADWindow> {
        logger.debug("Updating window: $id")
        
        return windowRepository.findById(id).flatMap { existing ->
            existing?.let { window ->
                // Update basic properties
                val updated = window.copy(
                    displayName = command.displayName?.let { 
                        DisplayName.create(it).getOrNull() 
                    } ?: window.displayName,
                    description = command.description?.let { 
                        Description.create(it).getOrNull() 
                    } ?: window.description,
                    isActive = command.isActive ?: window.isActive,
                    isSOTrx = command.isSOTrx ?: window.isSOTrx
                )

                // Save and publish event
                windowRepository.save(updated)
                    .onRight { saved -> 
                        publishWindowUpdated(saved) 
                    }
            } ?: WindowError.NotFound(id).left()
        }
    }

    override suspend fun findWindowByName(name: WindowName): Either<WindowError, ADWindow?> {
        return windowRepository.findByName(name)
    }

    override suspend fun searchWindows(query: String, pageSize: Int, page: Int): Flow<ADWindow> = flow {
        // Implement search logic here
        windowRepository.search(query, pageSize, page).collect { emit(it) }
    }

    @Transactional
    override suspend fun deleteWindow(id: UUID): Either<WindowError, Unit> {
        return windowRepository.delete(id)
            .onRight { publishWindowDeleted(id) }
    }

    private fun publishWindowCreated(window: ADWindow) {
        eventPublisher.publish(
            WindowEvent.WindowCreated(
                metadata = EventMetadata(
                    user = "system",
                    correlationId = UUID.randomUUID().toString()
                ),
                windowId = UUID.fromString(window.id),
                name = window.name.value,
                type = window.windowType
            )
        )
    }

    private fun publishWindowUpdated(window: ADWindow) {
        eventPublisher.publish(
            WindowEvent.WindowUpdated(
                metadata = EventMetadata(
                    user = "system",
                    correlationId = UUID.randomUUID().toString()
                ),
                windowId = UUID.fromString(window.id),
                changes = mapOf() // TODO: Track actual changes
            )
        )
    }

    private fun publishWindowDeleted(windowId: UUID) {
        eventPublisher.publish(
            WindowEvent.WindowDeleted(
                metadata = EventMetadata(
                    user = "system",
                    correlationId = UUID.randomUUID().toString()
                ),
                windowId = windowId
            )
        )
    }
}

// Command classes for window operations
data class CreateWindowCommand(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val tabs: List<CreateTabCommand> = emptyList(),
    val isActive: Boolean? = null,
    val isSOTrx: Boolean? = null,
    val windowType: WindowType? = null
)

data class CreateTabCommand(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val tableId: UUID,
    val sequence: Int? = null,
    val fields: List<CreateFieldCommand> = emptyList()
)

data class CreateFieldCommand(
    val columnName: String,
    val displayName: String,
    val description: String? = null,
    val isDisplayed: Boolean? = null,
    val isReadOnly: Boolean? = null,
    val isMandatory: Boolean? = null,
    val sequence: Int? = null,
    val displayLogic: String? = null,
    val defaultValue: String? = null,
    val validationRule: String? = null
)

data class UpdateWindowCommand(
    val displayName: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null,
    val isSOTrx: Boolean? = null
)
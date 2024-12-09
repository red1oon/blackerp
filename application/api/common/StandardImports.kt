package org.blackerp.application.api.common

import org.springframework.transaction.annotation.Transactional
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.shared.AuditInfo
import org.blackerp.domain.core.shared.VersionInfo
import org.blackerp.domain.events.DomainEvent
import org.blackerp.domain.events.DomainEventPublisher
import org.blackerp.domain.core.plugin.PluginRegistry
import arrow.core.Either
import arrow.core.right
import arrow.core.left
import kotlinx.coroutines.*
import java.util.UUID
import java.time.Instant

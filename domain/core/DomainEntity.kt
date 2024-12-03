package org.blackerp.domain.core

import java.util.UUID
import java.time.Instant

interface DomainEntity {
    val metadata: EntityMetadata 
}

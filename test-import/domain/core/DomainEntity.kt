package org.blackerp.domain.core

import org.blackerp.domain.core.metadata.EntityMetadata

interface DomainEntity {
    val metadata: EntityMetadata
}
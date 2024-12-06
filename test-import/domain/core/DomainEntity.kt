package org.blackerp.domain.core

import domain.core.shared.EntityMetadata

interface DomainEntity {
    val metadata: EntityMetadata
}
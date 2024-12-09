package org.blackerp.domain.core.ad.base

import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.shared.EntityMetadata
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description

interface ADObject : DomainEntity {
    override val id: String
    val metadata: EntityMetadata
    val displayName: DisplayName
    val description: Description?
}

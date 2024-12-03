package org.blackerp.domain.core.ad.base

import org.blackerp.domain.core.DomainEntity
import org.blackerp.domain.core.values.DisplayName
import org.blackerp.domain.core.values.Description

interface ADObject : DomainEntity {
    val displayName: DisplayName
    val description: Description?
}

package org.blackerp.domain.ad

import org.blackerp.domain.DomainEntity
import org.blackerp.domain.values.DisplayName
import org.blackerp.domain.values.Description

interface ADObject : DomainEntity {
    val displayName: DisplayName
    val description: Description?
}

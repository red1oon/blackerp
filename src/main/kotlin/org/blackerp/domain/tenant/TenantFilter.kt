package org.blackerp.domain.tenant

import arrow.core.Either
import arrow.core.right
import arrow.core.left
import org.blackerp.shared.ValidationError

data class TenantFilter(val tenantId: String?) {
    companion object {
        fun create(tenantId: String?): Either<ValidationError, TenantFilter> =
            when {
                tenantId == null -> TenantFilter(null).right()
                !tenantId.matches(Regex("^[0-9a-fA-F-]{36}$")) ->
                    ValidationError.InvalidFormat("Invalid UUID format").left()
                else -> TenantFilter(tenantId).right()
            }
    }
}

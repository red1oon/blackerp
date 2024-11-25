package org.blackerp.domain.table.relationship.value

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

@JvmInline
value class RelationshipName private constructor(val value: String) {
    companion object {
        fun create(value: String): Either<ValidationError, RelationshipName> =
            when {
                !value.matches(Regex("^[a-z][a-z0-9_]*$")) ->
                    ValidationError.InvalidFormat(
                        "Relationship name must start with lowercase letter and contain only lowercase letters, numbers, and underscores"
                    ).left()
                value.length !in 3..50 ->
                    ValidationError.InvalidLength("relationship name", 3, 50).left()
                else -> RelationshipName(value).right()
            }
    }
}

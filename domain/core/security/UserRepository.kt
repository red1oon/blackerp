package org.blackerp.domain.core.security

import arrow.core.Either
import java.util.UUID

interface UserRepository {
    suspend fun findByUsername(username: String): Either<SecurityError, User?>
    suspend fun findById(id: UUID): Either<SecurityError, User?>
    suspend fun save(user: User): Either<SecurityError, User>
}

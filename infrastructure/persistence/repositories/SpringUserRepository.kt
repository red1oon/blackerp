package org.blackerp.infrastructure.persistence.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.blackerp.infrastructure.persistence.entities.UserEntity
import java.util.UUID

@Repository
interface SpringUserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean
}

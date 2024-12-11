package org.blackerp.infrastructure.persistence.repositories

import org.blackerp.infrastructure.persistence.entities.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoleRepository : JpaRepository<RoleEntity, UUID> {
    fun findByName(name: String): RoleEntity?
    fun findByClientId(clientId: UUID): List<RoleEntity>
    fun findByClientIdAndIsActiveTrue(clientId: UUID): List<RoleEntity>
}

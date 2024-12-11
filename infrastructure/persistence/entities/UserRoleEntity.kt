package org.blackerp.infrastructure.persistence.entities

import jakarta.persistence.*
import java.util.UUID
import java.time.Instant

@Entity
@Table(name = "ad_role")
class RoleEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", unique = true)
    var name: String,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "client_id")
    var clientId: UUID,

    @Column(name = "organization_id")
    var organizationId: UUID? = null,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()
)

@Entity
@Table(name = "ad_user_role")
class UserRoleEntity(
    @EmbeddedId
    val id: UserRoleId,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)

@Embeddable
data class UserRoleId(
    @Column(name = "user_id")
    val userId: UUID,

    @Column(name = "role_id")
    val roleId: UUID
) : java.io.Serializable

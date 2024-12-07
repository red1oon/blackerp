package org.blackerp.infrastructure.persistence.entities

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ad_user")
class UserEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "username", unique = true)
    var username: String,

    @Column(name = "email")
    var email: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "client_id")
    var clientId: UUID,

    @Column(name = "organization_id")
    var organizationId: UUID? = null,

    @Column(name = "last_login")
    var lastLogin: Instant? = null,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "ad_user_role",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    var roleIds: MutableSet<UUID> = mutableSetOf()
)

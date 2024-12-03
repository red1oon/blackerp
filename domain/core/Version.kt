// File: domain/core/Version.kt
package org.blackerp.domain.core

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int
) {
    override fun toString() = "$major.$minor.$patch"
}
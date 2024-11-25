package org.blackerp.plugin

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.blackerp.shared.ValidationError

data class Version private constructor(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        return when {
            major != other.major -> major - other.major
            minor != other.minor -> minor - other.minor
            else -> patch - other.patch
        }
    }

    companion object {
        private val VERSION_PATTERN = Regex("^\\d+\\.\\d+\\.\\d+$")

        fun create(version: String): Either<ValidationError, Version> {
            if (!version.matches(VERSION_PATTERN)) {
                return ValidationError.InvalidFormat("Version must be in format major.minor.patch").left()
            }

            return try {
                val parts = version.split(".")
                if (parts.size != 3) {
                    ValidationError.InvalidFormat("Version must be in format major.minor.patch").left()
                } else {
                    Version(
                        major = parts[0].toInt(),
                        minor = parts[1].toInt(),
                        patch = parts[2].toInt()
                    ).right()
                }
            } catch (e: NumberFormatException) {
                ValidationError.InvalidFormat("Version components must be integers").left()
            }
        }
    }

    override fun toString(): String = "$major.$minor.$patch"
}
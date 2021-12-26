@file:Suppress("unused")

package io.github.wickedev.spring.security

import java.util.*

interface JwtDecoder {
    fun decode(token: String?): JWT?
}

interface JWT {
    val subject: String

    val type: Type

    val roles: List<String>

    val expiredAt: Date

    val isExpired: Boolean

    enum class Type {
        Access,
        Refresh,
    }
}
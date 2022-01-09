@file:Suppress("unused")

package io.github.wickedev.spring.security

import org.springframework.security.core.GrantedAuthority
import java.util.*

interface JwtDecoder {
    fun decode(token: String?): JWT?
}

interface JWT {
    val subject: String

    val type: Type

    val authorities: Collection<GrantedAuthority>

    val expiredAt: Date

    val isExpired: Boolean

    enum class Type {
        Access,
        Refresh,
    }
}
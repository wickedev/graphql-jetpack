package io.github.wickedev.spring.security.jwt

import org.springframework.security.core.GrantedAuthority
import java.util.*

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
@file:Suppress("unused")

package io.github.wickedev.spring.security

import org.springframework.security.core.userdetails.UserDetails
import java.time.Duration

interface JwtEncoder {
    fun encode(userDetails: UserDetails, type: JWT.Type, expiresIn: Duration?): Token

    fun renew(jwt: JWT, type: JWT.Type, expiresIn: Duration?): Token
}

data class Token(
    val value: String,
    val expiresIn: Long
)



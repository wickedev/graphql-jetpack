@file:Suppress("unused")

package io.github.wickedev.spring.security

import org.springframework.security.core.userdetails.UserDetails

interface JwtEncoder {
    fun encode(userDetails: UserDetails, type: JWT.Type, expiresIn: Long): Token

    fun renew(jwt: JWT, type: JWT.Type, expiresIn: Long): Token
}

data class Token(
    val value:String,
    val expiresIn: Long
)



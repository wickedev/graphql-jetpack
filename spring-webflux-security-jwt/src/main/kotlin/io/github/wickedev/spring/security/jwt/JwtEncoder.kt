@file:Suppress("unused")

package io.github.wickedev.spring.security.jwt

import org.springframework.security.core.userdetails.UserDetails
import java.time.Duration

interface JwtEncoder {
    fun encode(userDetails: UserDetails, type: JWT.Type, expiresIn: Duration?): Token

    fun renew(jwt: JWT, type: JWT.Type, expiresIn: Duration?): Token
}



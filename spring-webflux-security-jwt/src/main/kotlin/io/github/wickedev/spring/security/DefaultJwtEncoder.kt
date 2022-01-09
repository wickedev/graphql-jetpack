package io.github.wickedev.spring.security

import org.springframework.security.core.userdetails.UserDetails
import java.time.Duration

class DefaultJwtEncoder: JwtEncoder {
    override fun encode(userDetails: UserDetails, type: JWT.Type, expiresIn: Duration?): Token {
        TODO("Not yet implemented")
    }

    override fun renew(jwt: JWT, type: JWT.Type, expiresIn: Duration?): Token {
        TODO("Not yet implemented")
    }
}
package io.github.wickedev.spring.security.jwt

import io.github.wickedev.spring.security.jwt.JWT
import io.github.wickedev.spring.security.jwt.JwtEncoder
import io.github.wickedev.spring.security.jwt.Token
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
package io.github.wickedev.spring.security.jwt

import io.github.wickedev.spring.security.jwt.JWT
import io.github.wickedev.spring.security.jwt.JwtDecoder

class DefaultJwtDecoder: JwtDecoder {
    override fun decode(token: String?): JWT? {
        TODO("Not yet implemented")
    }
}
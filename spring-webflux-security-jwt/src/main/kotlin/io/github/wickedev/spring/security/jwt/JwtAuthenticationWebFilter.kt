package io.github.wickedev.spring.security.jwt

import io.github.wickedev.spring.security.BearerServerAuthenticationConverter
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

class JwtAuthenticationWebFilter(jwtDecoder: JwtDecoder): AuthenticationWebFilter(ReactiveJwtAuthenticationManager(jwtDecoder)) {
    init {
        setServerAuthenticationConverter(BearerServerAuthenticationConverter())
    }
}
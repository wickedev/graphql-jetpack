package io.github.wickedev.spring.security

import org.springframework.security.web.server.authentication.AuthenticationWebFilter

class JwtAuthenticationWebFilter(jwtDecoder: JwtDecoder): AuthenticationWebFilter(ReactiveJwtAuthenticationManager(jwtDecoder)) {
    init {
        setServerAuthenticationConverter(BearerServerAuthenticationConverter())
    }
}
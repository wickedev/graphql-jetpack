package io.github.wickedev.spring.security.jwt

import io.github.wickedev.spring.security.BearerTokenAuthenticationToken
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class ReactiveJwtAuthenticationManager(private val jwtDecoder: JwtDecoder) : ReactiveAuthenticationManager {


    override fun authenticate(authentication: Authentication): Mono<Authentication> = mono {

        if (authentication !is BearerTokenAuthenticationToken) {
            return@mono authentication
        }

        val jwt = jwtDecoder.decode(authentication.token)
            ?: throw BadCredentialsException("Invalid Credentials")

        if (jwt.isExpired) {
            throw BadCredentialsException("JWT expired at ${jwt.expiredAt}")
        }

        return@mono JwtAuthenticationToken(jwt)
    }
}
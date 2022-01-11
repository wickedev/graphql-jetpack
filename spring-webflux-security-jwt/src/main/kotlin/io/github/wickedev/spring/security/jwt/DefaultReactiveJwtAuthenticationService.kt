@file:Suppress("unused", "UNUSED_VARIABLE")

package io.github.wickedev.spring.security.jwt

import io.github.wickedev.coroutine.reactive.extensions.mono.await
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono

class DefaultReactiveJwtAuthenticationService(
    private val jwtProperties: JwtProperties,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: ReactiveUserDetailsService,
) : ReactiveJwtAuthenticationService {

    override fun signIn(username: String, password: String): Mono<AuthResponse?> = mono {
        val user = userDetailsService.findByUsername(username).await()

        if (!passwordEncoder.matches(password, user.password)) {
            return@mono null
        }

        val accessToken = jwtEncoder.encode(user, JWT.Type.Access, jwtProperties.accessTokenExpiresIn)
        val refreshToken = jwtEncoder.encode(user, JWT.Type.Refresh, jwtProperties.refreshTokenExpiresIn)

        return@mono AuthResponse(
            accessToken = accessToken.value,
            expiresIn = accessToken.expiresIn,
            refreshToken = refreshToken.value,
            refreshExpiresIn = refreshToken.expiresIn,
            scope = authoritiesToScope(user.authorities)
        )
    }

    override fun refresh(token: String?): Mono<AuthResponse?> = mono {
        if (token == null) {
            return@mono null
        }

        val jwt = jwtDecoder.decode(token)
            ?: return@mono null

        val accessToken = jwtEncoder.renew(jwt, JWT.Type.Access, jwtProperties.accessTokenExpiresIn)
        val refreshToken = jwtEncoder.renew(jwt, JWT.Type.Refresh, jwtProperties.refreshTokenExpiresIn)

        return@mono AuthResponse(
            accessToken = accessToken.value,
            expiresIn = accessToken.expiresIn,
            refreshToken = refreshToken.value,
            refreshExpiresIn = refreshToken.expiresIn,
            scope = authoritiesToScope(jwt.authorities)
        )
    }

    private fun authoritiesToScope(authorities: Collection<GrantedAuthority>): String {
        return authorities.joinToString { ", " }
    }
}
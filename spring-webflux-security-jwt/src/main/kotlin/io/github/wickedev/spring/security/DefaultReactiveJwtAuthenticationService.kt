@file:Suppress("unused", "UNUSED_VARIABLE")

package io.github.wickedev.spring.security

import io.github.wickedev.coroutine.reactive.extensions.mono.await
import kotlinx.coroutines.reactor.mono
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono

class DefaultReactiveJwtAuthenticationService(
    private val jwtProperties: JwtConfigurationProperties,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: ReactiveUserDetailsService,
) : ReactiveJwtAuthenticationService {
    private val refreshTokenKey = "refresh"

    @Throws(BadCredentialsException::class)
    override fun signIn(username: String, password: String, responseCookie: MultiValueMap<String, ResponseCookie>?): Mono<AuthResponse?> = mono {

        val user = userDetailsService.findByUsername(username).await()

        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("Invalid Credentials")
        }

        // val accessToken = jwtEncoder.encode(user, JWT.Type.Access, jwtProperties.accessTokenExpiresIn)
        // val refreshToken = jwtEncoder.encode(user, JWT.Type.Refresh, jwtProperties.refreshTokenExpiresIn)

        // setRefreshToken(responseCookie, refreshToken)

        return@mono  AuthResponse(
            accessToken = "accessToken.value",
            expiresIn =  0, // accessToken.expiresIn,
            refreshToken = "refreshToken.value",
        )

    }

    override fun refresh(token: String?, responseCookie: MultiValueMap<String, ResponseCookie>?): Mono<AuthResponse?> = mono {
        if (token == null) {
            return@mono null
        }

        val jwt = jwtDecoder.decode(token)
            ?: return@mono null

        // val accessToken = jwtEncoder.renew(jwt, JWT.Type.Access, jwtProperties.accessTokenExpiresIn)
        // val refreshToken = jwtEncoder.renew(jwt, JWT.Type.Refresh, jwtProperties.refreshTokenExpiresIn)

        // setRefreshToken(responseCookie, refreshToken)

        return@mono AuthResponse(
            accessToken = "accessToken.value",
            expiresIn = 1,
            refreshToken = "refreshToken.value",
        )
    }

    override fun signOut(responseCookie: MultiValueMap<String, ResponseCookie>?): Boolean {
        if (responseCookie == null) {
            return false
        }

        responseCookie.add(refreshTokenKey,
            ResponseCookie.from(refreshTokenKey, "deleted")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build()
        )

        return true
    }

    private fun setRefreshToken(responseCookie: MultiValueMap<String, ResponseCookie>?, token: Token): Boolean {
        if (responseCookie == null) {
            return false
        }

        responseCookie.add(refreshTokenKey,
            ResponseCookie.from(refreshTokenKey, token.value)
                .httpOnly(true)
                .secure(true)
                .maxAge(token.expiresIn)
                .build()
        )

        return true
    }
}
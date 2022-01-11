package io.github.wickedev.spring.security.jwt

import io.github.wickedev.spring.security.jwt.AuthResponse
import reactor.core.publisher.Mono

interface ReactiveJwtAuthenticationService {

    fun signIn(username: String, password: String): Mono<AuthResponse?>

    fun refresh(token: String?): Mono<AuthResponse?>
}
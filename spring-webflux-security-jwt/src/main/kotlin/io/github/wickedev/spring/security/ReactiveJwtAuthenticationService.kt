package io.github.wickedev.spring.security

import reactor.core.publisher.Mono

interface ReactiveJwtAuthenticationService {

    fun signIn(username: String, password: String): Mono<AuthResponse?>

    fun refresh(token: String?): Mono<AuthResponse?>
}
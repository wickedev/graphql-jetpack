package io.github.wickedev.spring.security

import org.springframework.http.ResponseCookie
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Mono

interface ReactiveJwtAuthenticationService {
    fun signIn(
        username: String,
        password: String,
        responseCookie: MultiValueMap<String, ResponseCookie>?
    ): Mono<AuthResponse?>

    fun refresh(token: String?, responseCookie: MultiValueMap<String, ResponseCookie>?): Mono<AuthResponse?>

    fun signOut(responseCookie: MultiValueMap<String, ResponseCookie>?): Boolean
}
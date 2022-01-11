package io.github.wickedev.spring.security.jwt

import org.springframework.security.core.AuthenticationException

class InvalidRefreshTokenException : AuthenticationException {

    constructor(msg: String?) : super(msg)

    constructor(msg: String?, cause: Throwable?) : super(msg, cause) {}
}
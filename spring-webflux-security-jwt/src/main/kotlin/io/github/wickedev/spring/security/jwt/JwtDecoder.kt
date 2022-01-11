@file:Suppress("unused")

package io.github.wickedev.spring.security.jwt

interface JwtDecoder {
    fun decode(token: String?): JWT?
}


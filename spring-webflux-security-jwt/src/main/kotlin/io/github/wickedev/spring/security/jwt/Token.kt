package io.github.wickedev.spring.security.jwt

data class Token(
    val value: String,
    val expiresIn: Long
)
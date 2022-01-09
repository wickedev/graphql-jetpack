package io.github.wickedev.spring.security

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val scope: String,
    val refreshToken: String,
    val refreshExpiresIn: Long,
)
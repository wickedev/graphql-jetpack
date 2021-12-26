package io.github.wickedev.spring.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("security.jwt")
data class JwtConfigurationProperties(
    val algorithm: String?,
    val privateKeyPath: String?,
    val publicKeyPath: String?,
    @Value("#{T(java.time.Duration).parse('\${security.jwt.access-token-expires-in}')}")
    val accessTokenExpiresIn: Duration?,
    @Value("#{T(java.time.Duration).parse('\${security.jwt.refresh-token-expires-in}')}")
    val refreshTokenExpiresIn: Duration?
)
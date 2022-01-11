package io.github.wickedev.spring.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("security.jwt")
class JwtProperties(
    val algorithm: String = "EC",
    val privateKeyPath: String? = "keys/ec256-private.pem",
    val publicKeyPath: String? = "keys/ec256-public.pem",
    val accessTokenExpiresIn: Duration? = Duration.ofHours(10),
    val refreshTokenExpiresIn: Duration? = Duration.ofDays(600),
)
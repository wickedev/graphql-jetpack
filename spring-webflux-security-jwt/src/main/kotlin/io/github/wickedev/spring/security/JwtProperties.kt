package io.github.wickedev.spring.security

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("security.jwt")
class JwtProperties(
    var algorithm: String = "EC",
    var privateKeyPath: String? = "keys/ec256-private.pem",
    var publicKeyPath: String? = "keys/ec256-public.pem",
    var accessTokenExpiresIn: Duration? = Duration.ofHours(10),
    var refreshTokenExpiresIn: Duration? = Duration.ofDays(600),
)
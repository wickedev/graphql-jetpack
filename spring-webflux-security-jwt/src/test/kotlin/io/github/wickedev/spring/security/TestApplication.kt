@file:Suppress("unused")

package io.github.wickedev.spring.security

import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.types.ID
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.util.*

data class User(
    val id: ID = ID.Empty,
    val email: String,
    val hashSalt: String,
    val roles: List<String>,
) : org.springframework.security.core.userdetails.User(email, hashSalt, roles.map { SimpleGrantedAuthority(it) })

data class AuthRequest(
    val email: String,
    val password: String,
)


class UserService : ReactiveUserDetailsService {
    override fun findByUsername(email: String): Mono<UserDetails> {
        return Mono.just(
            User(
                email = "orange881217@gmail.com",
                hashSalt = "${'$'}argon2id${'$'}v=19${'$'}m=4096,t=3,p=1${'$'}ZHXyVHYEoaxaX9pufhMllg${'$'}aQlLwOX6SUCTMQJFUUHzsfzacRpit2RkJv+I01bL10Y",
                roles = listOf("ROLE_USER")
            )
        )
    }
}

@SpringBootApplication
@EnableWebFluxSecurity
class TestApplication {

    @Bean
    fun authenticationManager(
        userDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder)
        }

    @Bean
    fun userService(): ReactiveUserDetailsService = UserService()

    @Bean
    fun roleHierarchy(): RoleHierarchy = DslRoleHierarchy {
        "ROLE_ADMIN" {
            "ROLE_MANAGER" {
                +"ROLE_USER"
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder()


    @Bean
    fun jwtAuthenticationService(
        jwtProperties: JwtConfigurationProperties,
        jwtEncoder: JwtEncoder,
        jwtDecoder: JwtDecoder,
        passwordEncoder: PasswordEncoder,
        userDetailsService: ReactiveUserDetailsService,
    ): ReactiveJwtAuthenticationService {
        return DefaultReactiveJwtAuthenticationService(
            jwtProperties,
            jwtEncoder,
            jwtDecoder,
            passwordEncoder,
            userDetailsService
        )
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return object : JwtDecoder {
            override fun decode(token: String?): JWT {
                return object : JWT {
                    override val subject: String
                        get() = "sub"
                    override val type: JWT.Type
                        get() = JWT.Type.Access
                    override val roles: List<String>
                        get() = emptyList()
                    override val expiredAt: Date
                        get() = Date()
                    override val isExpired: Boolean
                        get() = true
                }
            }
        }
    }

    @Bean
    fun configure(http: ServerHttpSecurity, jwtDecoder: JwtDecoder): SecurityWebFilterChain {
        http.csrf().disable()
        http.httpBasic().disable()
        http.formLogin().disable()
        http.logout().disable()
        http.authorizeExchange().pathMatchers("/graphql").permitAll()
        http.addFilterAt(JwtAuthenticationWebFilter(jwtDecoder), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }

    @Bean
    fun routes(jwtAuthenticationService: ReactiveJwtAuthenticationService) = coRouter {
        POST("/refresh") {
            val token = it.awaitBodyOrNull<String>()

            val authResponse = jwtAuthenticationService.refresh(token, it.exchange().response.cookies).await()
                ?: return@POST ServerResponse.badRequest().buildAndAwait()

            return@POST ServerResponse.ok().bodyValueAndAwait(authResponse)
        }
    }
}
@file:Suppress("unused")

package io.github.wickedev.spring.security

import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.types.ID
import io.github.wickedev.spring.security.jwt.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

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

@RestController
class AnnotatedController {
    @GetMapping("/allow")
    fun public(): Mono<ServerResponse> {
        return ServerResponse.ok().json().build()
    }

    @Secured("USER")
    @GetMapping("/protect/secured")
    fun secured(): Mono<ServerResponse> {
        return ServerResponse.ok().json().build()
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/protect/pre-authorize")
    fun preAuthorize(): Mono<ServerResponse> {
        return ServerResponse.ok().json().build()
    }
}

const val UNAUTHORIZED_STATUS_CODE = 401

@SpringBootApplication
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(JwtProperties::class)
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
        jwtProperties: JwtProperties,
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
    fun jwtEncoder(): JwtEncoder = DefaultJwtEncoder()

    @Bean
    fun jwtDecoder(): JwtDecoder = DefaultJwtDecoder()

    @Bean
    fun configure(http: ServerHttpSecurity, jwtDecoder: JwtDecoder): SecurityWebFilterChain {
        return http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            logout { disable() }
            authorizeExchange {
                authorize("/protect/user", hasRole("USER"))
                authorize("/protect/admin", hasRole("ADMIN"))
                authorize("/**", permitAll)
            }
            addFilterAt(JwtAuthenticationWebFilter(jwtDecoder), SecurityWebFiltersOrder.AUTHENTICATION)
        }
    }

    @Bean
    fun routes(jwtAuthenticationService: ReactiveJwtAuthenticationService) = coRouter {

        GET("/protect").nest {
            GET("user") {
                ServerResponse.ok().buildAndAwait()
            }
            GET("admin") {
                ServerResponse.ok().buildAndAwait()
            }
        }

        POST("/login") {
            val authRequest = it.awaitBodyOrNull<AuthRequest>()
                ?: return@POST ServerResponse.badRequest().buildAndAwait()

            val authResponse = jwtAuthenticationService.signIn(authRequest.email, authRequest.password).await()
                ?: return@POST ServerResponse.status(UNAUTHORIZED_STATUS_CODE).buildAndAwait()

            return@POST ServerResponse.ok().bodyValueAndAwait(authResponse)
        }

        POST("/refresh") {
            val token = it.awaitBodyOrNull<String>()

            val authResponse = jwtAuthenticationService.refresh(token).await()
                ?: return@POST ServerResponse.badRequest().buildAndAwait()

            return@POST ServerResponse.ok().bodyValueAndAwait(authResponse)
        }
    }
}
package io.github.wickedev.graphql


import com.expediagroup.graphql.server.operations.Query
import io.github.wickedev.graphql.types.ID
import io.github.wickedev.spring.security.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


data class User(
    val id: ID = ID.Empty,
    val email: String,
    val hashSalt: String,
    val roles: List<String>,
) : org.springframework.security.core.userdetails.User(email, hashSalt, roles.map { SimpleGrantedAuthority(it) })

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

@Suppress("unused")
@Component
class AuthQuery : Query {

    fun public(): Int = 1

    @Auth
    fun protected(): Int = 1

    @Auth("hasRole('USER')")
    fun protectedWithRole(): Int = 1

    @Auth("#param == 1")
    fun protectedWithParam(param: Int): Int = param

    @Auth("@checker.check(#param)")
    fun protectedWithCustomChecker(param: Int): Int = param
}

@Suppress("unused")
@Component
class Checker {
    fun check(param: Int): Boolean {
        return param == 1
    }
}

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
    fun configure(
        http: ServerHttpSecurity,
        jwtDecoder: JwtDecoder,
    ): SecurityWebFilterChain {
        http.csrf().disable()
        http.httpBasic().disable()
        http.formLogin().disable()
        http.logout().disable()
        http.authorizeExchange().pathMatchers("/graphql").permitAll()
        http.addFilterAt(JwtAuthenticationWebFilter(jwtDecoder), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }
}
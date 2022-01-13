package io.github.wickedev.graphql


import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import io.github.wickedev.graphql.types.ID
import io.github.wickedev.spring.reactive.security.DslRoleHierarchy
import io.github.wickedev.spring.reactive.security.EnableJwtWebFluxSecurity
import io.github.wickedev.spring.reactive.security.SimpleIdentifiableUserDetails
import io.github.wickedev.spring.reactive.security.decoder.JwtDecoder
import io.github.wickedev.spring.reactive.security.jwt.JwtAuthenticationWebFilter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.json
import reactor.core.publisher.Mono


data class User(
    val id: ID = ID.Empty,
    val email: String,
    val hashSalt: String,
    val roles: List<String>,
) : SimpleIdentifiableUserDetails<ID> {

    override fun getIdentifier(): ID = id

    override fun getUsername(): String = email

    override fun getPassword(): String = hashSalt

    override fun getAuthorities(): Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it) }
}

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

    @Auth("@checker.paramIsPositiveAndNameIsRyan(#param, #authentication)")
    fun protectedWithCustomChecker(param: Int, @GraphQLIgnore authentication: Authentication): Int = param
}

@Suppress("unused")
@Component
class Checker {
    fun paramIsPositiveAndNameIsRyan(param: Int, authentication: Authentication): Boolean {
        return param > 0 && authentication.name == "ryan"
    }
}


@RestController
class AnnotatedController {
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/protect/pre-authorize")
    fun preAuthorize(): Mono<ServerResponse> {
        return ServerResponse.ok().json().build()
    }
}

@SpringBootApplication
@EnableWebFluxSecurity
@EnableJwtWebFluxSecurity
@EnableReactiveMethodSecurity
class TestApplication {

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
    fun configure(
        http: ServerHttpSecurity,
        jwtDecoder: JwtDecoder,
    ): SecurityWebFilterChain {
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        http.csrf().disable()
        http.httpBasic().disable()
        http.formLogin().disable()
        http.logout().disable()
        http.authorizeExchange().pathMatchers("/graphql").permitAll()
        http.addFilterAt(JwtAuthenticationWebFilter(jwtDecoder), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }
}
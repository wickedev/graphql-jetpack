package io.github.wickedev.graphql


import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.server.operations.Query
import com.zhokhov.graphql.datetime.LocalDateTimeScalar
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.scalars.BigIntScalar
import io.github.wickedev.graphql.scalars.CustomScalars
import io.github.wickedev.graphql.types.ID
import io.github.wickedev.spring.security.DslRoleHierarchy
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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

    @Auth(requires = ["ROLE_USER"])
    fun protectedWithRole(): Int = 1

    @Auth
    fun protected(): Int = 1

    fun nonProtected(): Int = 1
}

@SpringBootApplication
@EnableWebFluxSecurity
class TestApplication {


    class MockAuthenticationWebFilter(authenticationManager: ReactiveAuthenticationManager) :
        AuthenticationWebFilter(authenticationManager) {
        override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = mono {
            val context: SecurityContext? = ReactiveSecurityContextHolder.getContext().await()

            if (context?.authentication == null) {
                super.filter(exchange, chain).await()
            } else {
                chain.filter(exchange).await()
            }
        }
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy = DslRoleHierarchy {
        "ROLE_ADMIN" {
            "ROLE_MANAGER" {
                +"ROLE_USER"
            }
        }
    }

    @Bean
    fun userService(): ReactiveUserDetailsService = UserService()

    @Bean
    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder()

    @Bean
    fun authenticationManager(
        userDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder)
        }

    @Bean
    fun authSchemaDirectiveWiring(roleHierarchy: RoleHierarchy) = AuthSchemaDirectiveWiring(roleHierarchy)

    @Bean
    fun directiveWiringFactory(authSchemaDirectiveWiring: AuthSchemaDirectiveWiring) =
        AuthDirectiveWiringFactory(authSchemaDirectiveWiring)

    @Bean
    fun schemaGeneratorHooks(
        resolvers: Optional<List<FederatedTypeResolver<*>>>,
        customScalars: CustomScalars,
        authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
    ) = AuthSchemaGeneratorHooks(resolvers.orElse(emptyList()), customScalars, authSchemaDirectiveWiring)

    @Bean
    fun customScalars(): CustomScalars {
        return CustomScalars.of(
            LocalDateTime::class to LocalDateTimeScalar.create(null, true, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            Long::class to BigIntScalar,
        )
    }

    @Bean
    fun configure(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager
    ): SecurityWebFilterChain {
        http.csrf().disable()
        http.httpBasic().disable()
        http.formLogin().disable()
        http.logout().disable()
        http.authorizeExchange().pathMatchers("/graphql").permitAll()
        http.addFilterAt(MockAuthenticationWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }

    @Bean
    fun graphQLContextFactory() = AuthGraphQLContextFactory()
}
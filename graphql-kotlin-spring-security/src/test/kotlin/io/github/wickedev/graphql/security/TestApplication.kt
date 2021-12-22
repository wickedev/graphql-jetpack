@file:Suppress("unused")

package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.types.ID
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.util.*


data class User(
    val id: ID = ID.Empty,
    val email: String,
    val hashSalt: String,
    val roles: List<String>,
) : GraphQLUser {
    override fun getUsername(): String = id.value

    override fun getPassword(): String = hashSalt

    override fun getAuthorities(): Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it) }
}

data class AuthRequest(
    val email: String,
    val password: String,
)

class CustomGraphQLContextFactory : SpringGraphQLContextFactory<SpringGraphQLContext>() {
    @Suppress("OverridingDeprecatedMember")
    override suspend fun generateContext(request: ServerRequest): SpringGraphQLContext? = null

    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> {
        val securityContext = ReactiveSecurityContextHolder.getContext().await()
        return securityContext?.let { mapOf("request" to request, "securityContext" to it) }
            ?: mapOf("request" to request)
    }
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

@Component
class AuthQuery(val authenticationManager: ReactiveAuthenticationManager) : Query {
    suspend fun login(email: String, password: String, env: DataFetchingEnvironment): Boolean {
        val token = UsernamePasswordAuthenticationToken(email, password)
        return authenticationManager.authenticate(token).await().isAuthenticated
    }

    @Auth(requires = ["ROLE_USER"])
    fun protected(): Int = 1
}

@SpringBootApplication
@EnableWebFluxSecurity
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
    fun authSchemaDirectiveWiring(roleHierarchy: RoleHierarchy) = AuthSchemaDirectiveWiring(roleHierarchy)

    @Bean
    fun directiveWiringFactory(authSchemaDirectiveWiring: AuthSchemaDirectiveWiring) =
        CustomDirectiveWiringFactory(authSchemaDirectiveWiring)

    @Bean
    fun schemaGeneratorHooks(
        resolvers: Optional<List<FederatedTypeResolver<*>>>,
        authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
    ) =
        CustomSchemaGeneratorHooks(resolvers.orElse(emptyList()), authSchemaDirectiveWiring)


    @Bean
    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder()

    @Bean
    fun authenticationManager(
        userService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager {
        return UserDetailsRepositoryReactiveAuthenticationManager(userService).apply {
            setPasswordEncoder(passwordEncoder)
        }
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

        http.authorizeExchange()
            .pathMatchers("/login", "/graphql")
            .permitAll()
            .pathMatchers("/allow-user")
            .hasRole("USER")
            .pathMatchers("/allow-admin")
            .hasRole("ADMIN")
            .anyExchange()
            .authenticated()

        return http.build()
    }

    @Bean
    fun graphQLContextFactory() = CustomGraphQLContextFactory()

    @Bean
    fun routes(authenticationManager: ReactiveAuthenticationManager) = coRouter {
        POST("/login") {
            val request = it.awaitBody<AuthRequest>()
            val token = UsernamePasswordAuthenticationToken(request.email, request.password)
            val authentication = authenticationManager.authenticate(token).await()
            val context = SecurityContextHolder.getContext()
            context.authentication = authentication
            ok().json().bodyValueAndAwait(authentication)
        }

        GET("/allow-user") {
            ok().json().buildAndAwait()
        }

        GET("/allow-admin") {
            ok().json().buildAndAwait()
        }
    }
}
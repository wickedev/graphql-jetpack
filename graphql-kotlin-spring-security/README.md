# GraphQL Kotlin Spring Security

Spring Security integration library with Custom GraphQL Directive `@auth`

[Inspired by With Custom Directives](https://www.apollographql.com/docs/apollo-server/security/authentication/#with-custom-directives)

# Getting Started

## Gradle

```kotlin
repositories {
    maven("https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo")
}

dependencies {
    implementation("io.github.wickedev:graphql-jetpack-starter:0.3.3")
}
```

## Example

```kotlin

@Configuration
class SecurityConfiguration {
    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf().disable()
        http.httpBasic().disable()
        http.formLogin().disable()
        http.logout().disable()
        http.authorizeExchange().pathMatchers("/graphql").permitAll()
        http.addFilterAt(MyAuthenticationWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy = DslRoleHierarchy {
        "ROLE_ADMIN" {
            "ROLE_MANAGER" {
                +"ROLE_USER"
            }
        }
    }
}

@Component
class Checker {
    fun paramIsPositiveAndNameIsRyan(param: Int, authentication: Authentication): Boolean {
        return param > 0 && authentication.name == "ryan"
    }
}

@Component
class SampleQuery : Query {

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
```

```graphql
directive @auth(require: String!) on FIELD | FIELD_DEFINITION

type Query {
    
    public: Int!
    
    protected: Int! @auth(require : "isAuthenticated")
    
    protectedWithRole: Int! @auth(require : "hasRole('USER')")
    
    protectedWithParam(param: Int!): Int! @auth(require : "#param == 1")
    
    protectedWithCustomChecker(param: Int!): Int! @auth(require : "@checker.paramIsPositiveAndNameIsRyan(#param, #authentication)")  
}

```
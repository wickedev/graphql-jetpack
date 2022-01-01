# GraphQL Kotlin Spring Security

Spring Security integration library with Custom GraphQL Directive `@auth`

[Inspired by With Custom Directives](https://www.apollographql.com/docs/apollo-server/security/authentication/#with-custom-directives)

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
class SampleQuery : Query {

    fun public(): Int = 1
    
    @Auth
    fun protected(): Int = 1

    @Auth(requires = ["ROLE_USER"])
    fun protectedWithRole(): Int = 1
}
```

```graphql
directive @auth(requires: [String!]!) on FIELD | FIELD_DEFINITION

type Query {

    public: Int!
    
    protected: Int! @auth
    
    protectedWithRole: Int! @auth(requires: ["ROLE_USER"])
}

```
package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import org.springframework.security.access.hierarchicalroles.RoleHierarchy

class AuthSchemaDirectiveWiring(private val roleHierarchy: RoleHierarchy) : KotlinSchemaDirectiveWiring {
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()

        val fetcher = AuthDataFetcher(originalDataFetcher, environment, roleHierarchy)
        environment.setDataFetcher(fetcher)

        return field
    }
}

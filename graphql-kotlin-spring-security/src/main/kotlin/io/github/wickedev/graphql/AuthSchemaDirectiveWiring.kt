package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy

class AuthSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()
        val fetcher = AuthDataFetcher(originalDataFetcher, environment)
        environment.setDataFetcher(fetcher)

        return field
    }
}

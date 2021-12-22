package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext

class AuthDataFetcher(
    private val originalDataFetcher: DataFetcher<*>,
    private val directiveEnv: KotlinFieldDirectiveEnvironment,
    private val roleHierarchy: RoleHierarchy
) : DataFetcher<Any> {
    private val rolePrefix = "ROLE_"

    override fun get(environment: DataFetchingEnvironment): Any {

        val authentication =
            environment.graphQlContext.getOrDefault<SecurityContext?>("securityContext", null)?.authentication

        val requires: Array<String> = if (directiveEnv.hasAuthDirective)
            @Suppress("UNCHECKED_CAST")
            directiveEnv.directive.getArgument("requires").argumentValue.value as? Array<String> ?: emptyArray()
        else
            emptyArray()

        val decision = vote(authentication, requires.map { AuthDirectiveConfigAttribute(it) })

        if (directiveEnv.hasAuthDirective && decision != AccessDecisionVoter.ACCESS_GRANTED) {
            return newError(ApolloError.AuthenticationError(), environment)
        }

        return originalDataFetcher.get(environment)
    }

    private fun newError(error: ApolloError, environment: DataFetchingEnvironment): DataFetcherResult<*> {
        val graphqlError = GraphqlErrorBuilder.newError(environment)
            .errorType(error)
            .message(error.message)
            .build()

        return DataFetcherResult.newResult<Any?>()
            .error(graphqlError)
            .build()
    }

    private fun vote(authentication: Authentication?, attributes: Collection<ConfigAttribute>): Int {
        if (authentication == null) {
            return AccessDecisionVoter.ACCESS_DENIED
        }
        var result = AccessDecisionVoter.ACCESS_ABSTAIN
        val authorities = roleHierarchy.getReachableGrantedAuthorities(authentication.authorities)
        for (attribute in attributes) {
            if (this.supports(attribute)) {
                result = AccessDecisionVoter.ACCESS_DENIED
                // Attempt to find a matching granted authority
                for (authority in authorities) {
                    if (attribute.attribute == authority.authority) {
                        return AccessDecisionVoter.ACCESS_GRANTED
                    }
                }
            }
        }
        return result
    }

    private fun supports(attribute: ConfigAttribute): Boolean {
        return attribute.attribute != null && attribute.attribute.startsWith(rolePrefix)
    }
}

package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class AuthDataFetcher(
    private val originalDataFetcher: DataFetcher<*>,
    private val directiveEnv: KotlinFieldDirectiveEnvironment,
) : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any? {
        if (originalDataFetcher is SpringSecurityFunctionDataFetcher) {
            val authentication = environment.graphQlContext.authentication
                ?: return newError(ApolloError.AuthenticationError(), environment)

            val expression = directiveEnv.requires ?: return originalDataFetcher.get(environment)
            val result = originalDataFetcher.check(authentication, expression, environment)

            if (!result) {
                return newError(ApolloError.ForbiddenError(), environment)
            }
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
}

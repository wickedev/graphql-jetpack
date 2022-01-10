package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.exceptions.ApolloError

open class AuthDataFetcher(
    private val originalDataFetcher: DataFetcher<*>,
    private val directiveEnv: KotlinFieldDirectiveEnvironment,
) : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any? {
        if (originalDataFetcher is JetpackFunctionDataFetcher) {
            val path = environment.executionStepInfo.path
            val sourceLocation = environment.field.sourceLocation

            val authentication = environment.graphQlContext.authentication
                ?: return newError(ApolloError.AuthenticationError(path, sourceLocation))

            val expression = directiveEnv.requires ?: return originalDataFetcher.get(environment)
            val result = originalDataFetcher.check(authentication, expression, environment)

            if (!result) {
                return newError(ApolloError.ForbiddenError(path, sourceLocation))
            }
        }

        return originalDataFetcher.get(environment)
    }

    private fun newError(error: ApolloError): DataFetcherResult<*> {
        return DataFetcherResult.newResult<Any?>()
            .error(error)
            .build()
    }
}

@file:Suppress("DEPRECATION")

package org.springframework.data.r2dbc.repository.support

import io.github.wickedev.graphql.spring.data.r2dbc.query.GraphQLCollectionPartTreeR2dbcQuery
import io.github.wickedev.graphql.spring.data.r2dbc.query.GraphQLConnectionPartTreeR2dbcQuery
import io.github.wickedev.graphql.spring.data.r2dbc.query.GraphQLR2dbcQueryMethod
import io.github.wickedev.graphql.spring.data.r2dbc.query.GraphQLSimplePartTreeR2dbcQuery
import io.github.wickedev.graphql.spring.data.r2dbc.query.redefine.redefineConnectionMethod
import io.github.wickedev.graphql.spring.data.r2dbc.query.redefine.redefineConnectionNextMethod
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.query.PartTreeR2dbcQuery
import org.springframework.data.r2dbc.repository.query.StringBasedR2dbcQuery
import org.springframework.data.repository.core.NamedQueries
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.query.QueryLookupStrategy
import org.springframework.data.repository.query.ReactiveQueryMethodEvaluationContextProvider
import org.springframework.data.repository.query.RepositoryQuery
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.lang.reflect.Method

internal class GraphQLR2dbcQueryLookupStrategy constructor(
    private val entityOperations: R2dbcEntityOperations,
    private val evaluationContextProvider: ReactiveQueryMethodEvaluationContextProvider,
    private val converter: R2dbcConverter,
    private val dataAccessStrategy: ReactiveDataAccessStrategy
) : QueryLookupStrategy {
    private val parser: ExpressionParser = CachingExpressionParser(SpelExpressionParser())

    override fun resolveQuery(
        method: Method, metadata: RepositoryMetadata, factory: ProjectionFactory,
        namedQueries: NamedQueries
    ): RepositoryQuery {
        val queryMethod = GraphQLR2dbcQueryMethod(
            method, metadata, factory,
            converter.mappingContext
        )
        val namedQueryName = queryMethod.namedQueryName
        return if (namedQueries.hasQuery(namedQueryName)) {
            val namedQuery = namedQueries.getQuery(namedQueryName)
            StringBasedR2dbcQuery(
                namedQuery, queryMethod, entityOperations, converter,
                dataAccessStrategy,
                parser, evaluationContextProvider
            )
        } else if (queryMethod.hasAnnotatedQuery()) {
            StringBasedR2dbcQuery(
                queryMethod, entityOperations, converter, dataAccessStrategy,
                parser,
                evaluationContextProvider
            )
        } else if (queryMethod.isConnectionQuery()) {
            GraphQLConnectionPartTreeR2dbcQuery(
                queryMethod,
                PartTreeR2dbcQuery(
                    GraphQLR2dbcQueryMethod(
                        redefineConnectionMethod(method, metadata), metadata, factory,
                        converter.mappingContext
                    ), entityOperations, converter, dataAccessStrategy
                ),
                PartTreeR2dbcQuery(
                    GraphQLR2dbcQueryMethod(
                        redefineConnectionNextMethod(method, metadata), metadata, factory,
                        converter.mappingContext
                    ), entityOperations, converter, dataAccessStrategy
                )
            )
        } else if (queryMethod.isCollectionGraphQLDataLoaderQuery()) {
            GraphQLCollectionPartTreeR2dbcQuery(
                method,
                metadata,
                factory,
                converter.mappingContext,
                entityOperations,
                converter,
                dataAccessStrategy
            )
        } else if (queryMethod.isGraphQLDataLoaderQuery()) {
            GraphQLSimplePartTreeR2dbcQuery(
                method,
                metadata,
                factory,
                converter.mappingContext,
                entityOperations,
                converter,
                dataAccessStrategy
            )
        } else {
            PartTreeR2dbcQuery(queryMethod, entityOperations, converter, dataAccessStrategy)
        }
    }
}
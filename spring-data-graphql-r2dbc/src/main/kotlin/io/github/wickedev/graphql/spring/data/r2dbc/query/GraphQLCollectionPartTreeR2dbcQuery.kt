@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.query

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import org.reactivestreams.Publisher
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.query.AbstractGraphQRepositoryQuery
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.repository.query.RelationalParameterAccessor
import org.springframework.data.relational.repository.query.RelationalParametersParameterAccessor
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.r2dbc.core.PreparedOperation
import org.springframework.r2dbc.core.RowsFetchSpec
import java.lang.reflect.Method


class GraphQLCollectionPartTreeR2dbcQuery(
    method: Method,
    metadata: RepositoryMetadata,
    factory: ProjectionFactory,
    mappingContext: MappingContext<out RelationalPersistentEntity<*>, out RelationalPersistentProperty>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter,
    dataAccessStrategy: ReactiveDataAccessStrategy
) : AbstractGraphQRepositoryQuery(
    GraphQLR2dbcQueryMethod(redefineMethod(method, metadata), metadata, factory, mappingContext),
    entityOperations,
    converter,
    dataAccessStrategy
) {

    override fun execute(parameters: Array<Any?>): Any? {
        val dataLoaderKey = method.toString()
        val env: DataFetchingEnvironment =
            parameters.find { it is DataFetchingEnvironment } as? DataFetchingEnvironment
                ?: throw Error("No DataFetchingEnvironment parameter candidate on ${method.name}.")
        val filteredParam = parameters.filter { it !is DataFetchingEnvironment }.toTypedArray()

        return env.dataLoader<Array<Any?>, Any?>(dataLoaderKey) { keys ->
            keys.map {
                val parameterAccessor: RelationalParameterAccessor =
                    RelationalParametersParameterAccessor(method, it)
                val query = createQuery(parameterAccessor)
                    .flatMapMany { q -> executeQuery(parameterAccessor, q) }
                query.collectList().await()
            }
        }.load(filteredParam)
    }

    private fun executeQuery(
        parameterAccessor: RelationalParameterAccessor,
        operation: PreparedOperation<*>,
    ): Publisher<*> {
        val processor = method.resultProcessor.withDynamicProjection(parameterAccessor)
        val fetchSpec: RowsFetchSpec<*> =  entityOperations.query(operation, resolveResultType(processor))
        return execute(fetchSpec)
    }

}

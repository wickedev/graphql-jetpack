@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.query

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.camelToSnakeCase
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.spring.data.r2dbc.query.redefine.redefineMethod
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeFiller
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.reactive.awaitFirst
import org.reactivestreams.Publisher
import org.springframework.data.mapping.PropertyPath
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.r2dbc.convert.EntityRowMapper
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.query.AbstractGraphQRepositoryQuery
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.repository.query.RelationalParameterAccessor
import org.springframework.data.relational.repository.query.RelationalParametersParameterAccessor
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.query.parser.PartTree
import org.springframework.r2dbc.core.PreparedOperation
import org.springframework.r2dbc.core.RowsFetchSpec
import java.lang.reflect.Method


class GraphQLSimplePartTreeR2dbcQuery(
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
    private val idTypeFiller = IDTypeFiller(mappingContext)

    data class KeyValue(
        val keys: List<Any?>,
        val value: Any?
    )

    override fun isModifyingQuery(): Boolean {
        return tree.isDelete
    }

    override fun isCountQuery(): Boolean {
        return tree.isCountProjection
    }

    override fun isExistsQuery(): Boolean {
        return tree.isExistsProjection
    }

    override fun execute(parameters: Array<Any?>): Any? {
        val dataLoaderKey = method.toString()
        val env: DataFetchingEnvironment =
            parameters.find { it is DataFetchingEnvironment } as? DataFetchingEnvironment
                ?: throw Error("No DataFetchingEnvironment parameter candidate on ${method.name}.")
        val filteredParam = parameters.filter { it !is DataFetchingEnvironment }.toTypedArray()

        val tree = PartTree(method.name, method.entityInformation.javaType)
        val segment = tree.parts.map { it.property }
            .filter { it.type != DataFetchingEnvironment::class.java }
            .toList()

        return env.dataLoader<Array<Any?>, Any?>(dataLoaderKey) { keys ->

            return@dataLoader if (isExistsQuery || isCountQuery) {
                keys.map { key ->
                    val parameterAccessor: RelationalParameterAccessor =
                        RelationalParametersParameterAccessor(method, key)
                    val query = createQuery(parameterAccessor)
                        .flatMapMany { q -> executeQuery(parameterAccessor, q, segment) }
                    query.map { it.value }.awaitFirst()
                }
            } else {

                val parameterAccessor: RelationalParameterAccessor =
                    RelationalParametersParameterAccessor(method, keys.toTypedArray())
                val query = createQuery(parameterAccessor)
                    .flatMapMany { q -> executeQuery(parameterAccessor, q, segment) }
                val results = query.collectList().await()
                keys.map { k -> results.find { r -> r.keys == k.toList() }?.value }
                    .map { idTypeFiller.setIDTypeIfNecessary(it as Any) }
            }
        }.load(filteredParam)
    }

    private fun executeQuery(
        parameterAccessor: RelationalParameterAccessor,
        operation: PreparedOperation<*>,
        keyPaths: List<PropertyPath>
    ): Publisher<KeyValue> {
        val processor = method.resultProcessor.withDynamicProjection(parameterAccessor)
        val typeToRead = resolveResultType(processor)
        val rowMapper: EntityRowMapper<*> = EntityRowMapper(typeToRead, converter)
        val fetchSpec: RowsFetchSpec<KeyValue> = if (isExistsQuery) {
            entityOperations.databaseClient.sql(operation).map { _ -> KeyValue(emptyList(), true) }
        } else if (isCountQuery) {
            entityOperations.databaseClient.sql(operation).map { row: Row, rowMetadata: RowMetadata ->
                val value = rowMapper.apply(row, rowMetadata)
                KeyValue(emptyList(), value)
            }
        } else {
            val returnType = resolveResultType(processor)
            entityOperations.query(operation, resolveResultType(processor)) { row: Row, rowMetadata: RowMetadata ->
                val keys = keyPaths.map {
                    val column = it.segment.camelToSnakeCase()
                    converter.readValue(row.get(column), it.typeInformation)
                }

                val value = converter.read(returnType, row, rowMetadata)
                KeyValue(keys, value)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return execute(fetchSpec) as Publisher<KeyValue>
    }
}
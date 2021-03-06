package io.github.wickedev.graphql.spring.data.r2dbc.query

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.types.Connection
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.r2dbc.repository.query.R2dbcQueryMethod
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.util.ClassUtils
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture

class GraphQLR2dbcQueryMethod(
    private val method: Method,
    metadata: RepositoryMetadata,
    projectionFactory: ProjectionFactory,
    mappingContext: MappingContext<out RelationalPersistentEntity<*>, out RelationalPersistentProperty>
) : R2dbcQueryMethod(
    method,
    metadata,
    projectionFactory,
    mappingContext
) {

    private fun hasDataFetchingEnvironmentParameter(): Boolean {
        return ClassUtils.hasParameterOfType(method, DataFetchingEnvironment::class.java)
    }

    private fun returnTypeIsCompletableFuture(): Boolean {
        return method.returnType === CompletableFuture::class.java
    }

    fun isGraphQLDataLoaderQuery(): Boolean {
        return hasDataFetchingEnvironmentParameter() && returnTypeIsCompletableFuture() && !isModifyingQuery
    }

    fun isConnectionQuery(): Boolean {
        return method.name.startsWith("connection") && returnedObjectType == Connection::class.java
    }

    fun isCollectionGraphQLDataLoaderQuery(): Boolean {
        return isGraphQLDataLoaderQuery() && this.isCollectionQuery
    }
}
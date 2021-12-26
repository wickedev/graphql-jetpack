package io.github.wickedev.graphql.spring.data.r2dbc.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderByIdRepository
import io.github.wickedev.graphql.repository.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.`as`
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.tableName
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeStrategy
import io.github.wickedev.graphql.types.ID
import org.springframework.beans.factory.ObjectProvider
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata
import org.springframework.util.ClassUtils
import java.util.concurrent.CompletableFuture

class SimpleGraphQLNodeRepository(
    private val repositories: ObjectProvider<GraphQLDataLoaderByIdRepository<*>>,
    private val idTypeStrategy: IDTypeStrategy,
) : GraphQLNodeRepository {

    private val tables: Map<String, GraphQLDataLoaderByIdRepository<Node>> by lazy {
        repositories.asSequence()
            .map { ClassUtils.getAllInterfaces(it).firstOrNull() to it }
            .filter { it.first != null }
            .`as`<Pair<Class<*>, GraphQLDataLoaderByIdRepository<Node>>>()
            .map { AbstractRepositoryMetadata.getMetadata(it.first) to it.second }
            .filter { Node::class.java.isAssignableFrom(it.first.domainType) }
            .filter { ID::class.java.isAssignableFrom(it.first.idType) }
            .associate { idTypeStrategy.convertType(it.first.domainType, it.first.tableName) to it.second }
            .filter { it.key.isNotEmpty() }
    }

    override fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node?> {
        return tables[id.type]?.findById(id, env) ?: CompletableFuture.completedFuture(null)
    }
}
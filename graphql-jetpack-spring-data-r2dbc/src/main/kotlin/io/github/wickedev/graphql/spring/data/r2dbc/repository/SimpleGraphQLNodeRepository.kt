package io.github.wickedev.graphql.spring.data.r2dbc.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.extentions.`as`
import io.github.wickedev.extentions.name
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import org.springframework.beans.factory.ObjectProvider
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata
import org.springframework.util.ClassUtils
import java.util.concurrent.CompletableFuture

class SimpleGraphQLNodeRepository(
    private val repositories: ObjectProvider<GraphQLDataLoaderRepository<*>>,
) : GraphQLNodeRepository {

    private val tables: Map<String, GraphQLDataLoaderRepository<Node>> by lazy {
        repositories.asSequence()
            .map { ClassUtils.getAllInterfaces(it).firstOrNull() to it }
            .filter { it.first != null }
            .`as`<Pair<Class<*>, GraphQLDataLoaderRepository<Node>>>()
            .map { AbstractRepositoryMetadata.getMetadata(it.first) to it.second }
            .filter { Node::class.java.isAssignableFrom(it.first.domainType) }
            .filter { ID::class.java.isAssignableFrom(it.first.idType) }
            .associate { it.first.name to it.second }
            .filter { it.key.isNotEmpty() }
    }

    override fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node?> {
        return tables[id.type]?.findById(id, env) ?: CompletableFuture.completedFuture(null)
    }
}
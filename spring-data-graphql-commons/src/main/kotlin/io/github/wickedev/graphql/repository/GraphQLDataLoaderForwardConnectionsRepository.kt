package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.Forward
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderForwardConnectionsRepository<T : Node> : GraphQLDataLoaderRepository<T> {
    fun connection(
        forward: Forward, env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>

    fun connection(
        forward: Forward, criteria: Criteria, env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>
}

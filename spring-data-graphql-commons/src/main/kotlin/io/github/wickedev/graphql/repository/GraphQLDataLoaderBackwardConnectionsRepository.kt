package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.Sort
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderBackwardConnectionsRepository<T : Node> : GraphQLDataLoaderRepository<T> {
    fun connection(
        backward: Backward, env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>

    fun connection(
        backward: Backward, criteria: Criteria, env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>
}

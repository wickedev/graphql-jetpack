package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.Forward
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepository<T: Node> : GraphQLDataLoaderRepository<T> {


    fun connection(backward: Backward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>

    fun connection(forward: Forward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>
}

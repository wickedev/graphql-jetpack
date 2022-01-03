package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepository<T: Node> : GraphQLDataLoaderRepository<T> {

    fun connectionBackwardById(last: Int?, before: String?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>

    fun connectionForwardById(first: Int?, after: String?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>

    fun connectionBackwardById(last: Int?, before: ID?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>

    fun connectionForwardById(first: Int?, after: ID?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>
}

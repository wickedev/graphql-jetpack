package io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.value.Connection
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepository<T: Node> : Repository<T, ID> {

    fun findAllBackward(last: Int?, before: ID?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>

    fun findAllForward(first: Int?, after: ID?, env: DataFetchingEnvironment): CompletableFuture<Connection<T>>
}

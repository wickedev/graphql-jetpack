package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderNodeRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderNodeRepositoryMixin<T : Node> : GraphQLDataLoaderNodeRepository<T> {

    override fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node> {
        TODO("Not yet implemented")
    }
}
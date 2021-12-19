package io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLNodeRepository : Repository<Node, ID> {

    fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node?>
}
package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLNodeRepository : Repository<Node, ID> {

    fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node?>
}
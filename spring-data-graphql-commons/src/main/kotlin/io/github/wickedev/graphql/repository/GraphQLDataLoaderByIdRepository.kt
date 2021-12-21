package io.github.wickedev.graphql.repository

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderByIdRepository<T : Node> : GraphQLDataLoaderRepository<T> {

    fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T?>

    fun findAllById(ids: Iterable<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>>

    fun existsById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean>
}
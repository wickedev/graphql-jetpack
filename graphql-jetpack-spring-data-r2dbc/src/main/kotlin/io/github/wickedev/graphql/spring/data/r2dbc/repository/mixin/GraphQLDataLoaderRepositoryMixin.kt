package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import org.reactivestreams.Publisher
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderRepositoryMixin<T, ID> : GraphQLDataLoaderRepository<T, ID> {

    override fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T?> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<T?> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun findAll(env: DataFetchingEnvironment): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: Iterable<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun findAllById(idStream: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>> {
        TODO("Not yet implemented")
    }

    override fun count(env: DataFetchingEnvironment): CompletableFuture<Long> {
        TODO("Not yet implemented")
    }
}
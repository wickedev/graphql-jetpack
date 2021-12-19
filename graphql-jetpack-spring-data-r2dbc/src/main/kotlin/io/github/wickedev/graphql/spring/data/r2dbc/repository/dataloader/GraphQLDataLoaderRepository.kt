package io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader

import graphql.schema.DataFetchingEnvironment
import org.reactivestreams.Publisher
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderRepository<T,ID>: Repository<T, ID> {

    fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T?>

    fun findById(id: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<T?>

    fun existsById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean>
    
    fun existsById(id: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<Boolean>

    fun findAll(env: DataFetchingEnvironment): CompletableFuture<List<T>>

    fun findAllById(ids: Iterable<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>>

    fun findAllById(idStream: Publisher<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>>

    fun count(env: DataFetchingEnvironment): CompletableFuture<Long>
}
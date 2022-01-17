package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderByIdRepository
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderByIdRepositoryMixin<T : Node?> : GraphQLDataLoaderByIdRepository<T>,
    R2dbcAllInQueryMixin<T, ID> {

    override fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T> {
        val key = "${information.repositoryInterface.canonicalName}.findById(ID)"

        return env.dataLoader<ID, T>(key) { keys ->
            val results = findAllByIdsIn(keys).collectList().await()
            @Suppress("UNCHECKED_CAST")
            keys.map { k -> results.find { r -> r?.id?.value == k.value } } as List<T>
        }.load(id)
    }

    override fun findAllById(ids: Iterable<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>> {
        val key = "${information.repositoryInterface.canonicalName}.findAllById(Iterable<ID>)"
        return env.dataLoader<Iterable<ID>, List<T>>(key) { keys ->
            keys.map { findAllByIdsIn(it.toList()).collectList().await() }
        }.load(ids)
    }

    override fun existsById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean> {
        val key = "${information.repositoryInterface.canonicalName}.existsById(ID)"

        return env.dataLoader<ID, Boolean>(key) { keys ->
            val existsIds = findAllIdByIdsIn(keys).map { it.toString() }.collectList().await()
            keys.map { k -> existsIds.contains(k.value) }
        }.load(id)
    }
}

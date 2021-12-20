package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.extentions.dataLoader
import io.github.wickedev.graphql.extentions.mono.await
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import org.springframework.data.r2dbc.core.R2dbcAllInQueryMixin
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderRepositoryMixin<T : Node> : GraphQLDataLoaderRepository<T>, R2dbcAllInQueryMixin<T, ID> {

    override fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T?> {
        val key = "${information.repositoryInterface.canonicalName}.findById(ID)"

        return env.dataLoader<ID, T?>(key) { keys ->
            val results = findAllByIdsIn(keys).collectList().await()
            keys.map { k -> results.find { r -> r.id.value == k.value } }
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

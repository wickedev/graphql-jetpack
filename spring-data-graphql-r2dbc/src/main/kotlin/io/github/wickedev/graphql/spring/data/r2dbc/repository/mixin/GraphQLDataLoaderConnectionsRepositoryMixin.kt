package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import io.github.wickedev.graphql.types.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.data.domain.Sort
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepositoryMixin<T : Node> : GraphQLDataLoaderConnectionsRepository<T>,
    PropertyRepository<T, ID>, R2dbcRepositoryMixin<T, ID> {

    override fun connection(backward: Backward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {
        val key =
            "${information.repositoryInterface.canonicalName}.findAllBackwardConnectById(Int,ID,DataFetchingEnvironment)"
        return env.dataLoader<Backward, Connection<T>>(key) { keys ->
            keys.map { backwardPagination(it.last ?: DEFAULT_CONNECTION_SIZE, it.before).await() }
        }.load(backward)
    }

    override fun connection(forward: Forward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {


        val key =
            "${information.repositoryInterface.canonicalName}.findAllForwardConnectById(Int,ID,DataFetchingEnvironment)"
        return env.dataLoader<Forward, Connection<T>>(key) { keys ->
            keys.map { forwardPagination(it.first ?: DEFAULT_CONNECTION_SIZE, it.after).await() }
        }.load(forward)
    }

    fun whereIdGreaterThanCriteria(after: ID?): Criteria {
        return whereId().greaterThan(after as Any)
    }

    fun whereIdGreaterThanQuery(after: ID): Query {
        return Query.query(whereIdGreaterThanCriteria(after))
    }

    fun whereIdLessThanCriteria(before: ID?): Criteria {
        return whereId().lessThan(before as Any)
    }

    fun whereIdLessThanQuery(before: ID?): Query {
        return Query.query(whereIdLessThanCriteria(before))
    }

    fun findFirst(direction: Sort.Direction): Mono<T?> {
        val idProperty = getIdProperty().name
        val sort = Sort.by(direction, idProperty)

        return entityOperations.select(
            emptyQuery().sort(sort).limit(1), entity.javaType
        ).toMono()
    }

    fun backwardPagination(last: Int, before: ID?): Mono<Connection<T>> = mono {
        val start = findFirst(Sort.Direction.DESC).awaitSingleOrNull()

        val query = (if (before == null || before.value.isEmpty())
            Query.empty()
        else whereIdLessThanQuery(before))
            .limit(last + 1)
            .sort(Sort.by(getIdProperty().name).descending())

        val items = entityOperations.select(query, entity.javaType).await()

        val edges = items.take(last)

        Connection(
            edges = edges.map { Edge(it, it.id) }, pageInfo = PageInfo(
                hasPreviousPage = items.size > last,
                hasNextPage = edges.firstOrNull()?.id != start?.id,
                startCursor = edges.lastOrNull()?.id?.encoded ?: "",
                endCursor = edges.firstOrNull()?.id?.encoded ?: "",
            )
        )
    }

    fun forwardPagination(first: Int, after: ID?): Mono<Connection<T>> = mono {
        val start = findFirst(Sort.Direction.ASC).awaitSingleOrNull()

        val query = (if (after == null || after.value.isEmpty())
            Query.empty()
        else whereIdGreaterThanQuery(after))
            .limit(first + 1)
            .sort(Sort.by(getIdProperty().name).ascending())

        val items = entityOperations.select(query, entity.javaType).await()

        val edges = items.take(first)

        Connection(
            edges = edges.map { Edge(it, it.id) }, pageInfo = PageInfo(
                hasPreviousPage = edges.firstOrNull()?.id != start?.id,
                hasNextPage = items.size > first,
                startCursor = edges.firstOrNull()?.id?.encoded ?: "",
                endCursor = edges.lastOrNull()?.id?.encoded ?: "",
            )
        )
    }
}

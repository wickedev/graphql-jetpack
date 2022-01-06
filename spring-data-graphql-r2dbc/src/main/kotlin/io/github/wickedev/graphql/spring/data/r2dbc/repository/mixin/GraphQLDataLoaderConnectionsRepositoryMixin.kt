package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.inverted
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

    companion object {
        const val DEFAULT_EDGES_SIZE = 10
    }


    override fun connection(backward: Backward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {
        val (last: Int?, before: ID?) = backward

        val key =
            "${information.repositoryInterface.canonicalName}.findAllBackwardConnectById(Int,ID,DataFetchingEnvironment)"
        return env.dataLoader<Backward, Connection<T>>(key) { keys ->
            keys.map { backwardPagination(it.last, it.before).await() }
        }.load(Backward(last, before))
    }

    override fun connection(forward: Forward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {
        val (first: Int?, after: ID?) = forward

        val key =
            "${information.repositoryInterface.canonicalName}.findAllForwardConnectById(Int,ID,DataFetchingEnvironment)"
        return env.dataLoader<Forward, Connection<T>>(key) { keys ->
            keys.map { forwardPagination(it.first, it.after).await() }
        }.load(Forward(first, after))
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

    fun findLast(direction: Sort.Direction): Mono<T?> {
        val idProperty = getIdProperty().name
        val sort = Sort.by(direction.inverted, idProperty)

        return entityOperations.select(
            emptyQuery().sort(sort).limit(1), entity.javaType
        ).toMono()
    }

    fun backwardPagination(last: Int?, before: ID?): Mono<Connection<T>> = mono {
        val edgesSize = last ?: DEFAULT_EDGES_SIZE
        val startOfAll = findFirst(Sort.Direction.DESC).awaitSingleOrNull()?.id?.value
        val endOfAll = findLast(Sort.Direction.DESC).awaitSingleOrNull()?.id?.value

        val query =
            (if (before == null || before.value.isEmpty()) Query.empty() else whereIdLessThanQuery(before)).limit(
                edgesSize
            ).sort(Sort.by(getIdProperty().name).descending())

        val edges = entityOperations.select(query, entity.javaType).await()

        val startOfEdge = edges.firstOrNull()?.id?.value ?: ""
        val endOfEdge = edges.lastOrNull()?.id?.value ?: ""

        Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) }, pageInfo = PageInfo(
                hasPreviousPage = endOfAll != endOfEdge,
                hasNextPage = startOfAll != startOfEdge,
                startCursor = endOfEdge,
                endCursor = startOfEdge
            )
        )
    }

    fun forwardPagination(first: Int?, after: ID?): Mono<Connection<T>> = mono {
        val edgesSize = first ?: DEFAULT_EDGES_SIZE
        val startOfAll = findFirst(Sort.Direction.ASC).awaitSingleOrNull()?.id?.value
        val endOfAll = findLast(Sort.Direction.ASC).awaitSingleOrNull()?.id?.value

        val query =
            (if (after == null || after.value.isEmpty()) Query.empty() else whereIdGreaterThanQuery(after)).limit(
                edgesSize
            ).sort(Sort.by(getIdProperty().name).ascending())

        val edges = entityOperations.select(query, entity.javaType).await()

        val startOfEdge = edges.firstOrNull()?.id?.value ?: ""
        val endOfEdge = edges.lastOrNull()?.id?.value ?: ""

        Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) }, pageInfo = PageInfo(
                hasPreviousPage = startOfAll != startOfEdge,
                hasNextPage = endOfAll != endOfEdge,
                startCursor = startOfEdge,
                endCursor = endOfEdge
            )
        )
    }
}

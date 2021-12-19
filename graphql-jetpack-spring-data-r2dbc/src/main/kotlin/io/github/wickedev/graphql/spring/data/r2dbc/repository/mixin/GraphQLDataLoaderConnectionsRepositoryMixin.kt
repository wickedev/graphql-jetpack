package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.extentions.flux.await
import io.github.wickedev.extentions.inverted
import io.github.wickedev.extentions.mono.await
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.value.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.dataloader.DataLoaderFactory
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryMixin
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

    override fun findAllBackward(
        last: Int?,
        before: ID?,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>> {
        val key = "${information.repositoryInterface.canonicalName}.findAllBackward"

        return env.dataLoaderRegistry.computeIfAbsent<Backward, Connection<T>>(key) {
            DataLoaderFactory.newDataLoader<Backward, Connection<T>> { keys ->
                CoroutineScope(Dispatchers.Unconfined).future {
                    keys.map { backwardPagination(it.last, it.before).await() }
                }

            }
        }.load(Backward(last, before))
    }


    override fun findAllForward(
        first: Int?,
        after: ID?,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>> {
        val key = "${information.repositoryInterface.canonicalName}.findAllForward"
        return env.dataLoaderRegistry.computeIfAbsent<Forward, Connection<T>>(key) {
            DataLoaderFactory.newDataLoader<Forward, Connection<T>> { keys ->

                CoroutineScope(Dispatchers.Unconfined).future {
                    keys.map { forwardPagination(it.first, it.after).await() }
                }

            }
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
            emptyQuery().sort(sort).limit(1),
            entity.javaType
        ).toMono()
    }

    fun findLast(direction: Sort.Direction): Mono<T?> {
        val idProperty = getIdProperty().name
        val sort = Sort.by(direction.inverted, idProperty)

        return entityOperations.select(
            emptyQuery().sort(sort).limit(1),
            entity.javaType
        ).toMono()
    }

    fun backwardPagination(last: Int?, before: ID?): Mono<Connection<T>> = mono {
        val edgesSize = last ?: DEFAULT_EDGES_SIZE
        val startOfAll = findFirst(Sort.Direction.DESC).awaitSingleOrNull()?.id?.value
        val endOfAll = findLast(Sort.Direction.DESC).awaitSingleOrNull()?.id?.value

        val query = (if (before == null || before.value.isEmpty()) Query.empty() else whereIdLessThanQuery(before))
            .limit(edgesSize)
            .sort(Sort.by(getIdProperty().name).descending())

        val edges = entityOperations.select(query, entity.javaType).await()

        val startOfEdge = edges.firstOrNull()?.id?.value ?: ""
        val endOfEdge = edges.lastOrNull()?.id?.value ?: ""

        Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) },
            pageInfo = PageInfo(
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

        val query = (if (after == null || after.value.isEmpty()) Query.empty() else whereIdGreaterThanQuery(after))
            .limit(edgesSize)
            .sort(Sort.by(getIdProperty().name).ascending())

        val edges = entityOperations.select(query, entity.javaType).await()

        val startOfEdge = edges.firstOrNull()?.id?.value ?: ""
        val endOfEdge = edges.lastOrNull()?.id?.value ?: ""

        Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) },
            pageInfo = PageInfo(
                hasPreviousPage = startOfAll != startOfEdge,
                hasNextPage = endOfAll != endOfEdge,
                startCursor = startOfEdge,
                endCursor = endOfEdge
            )
        )
    }
}

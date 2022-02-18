package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderForwardConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.toSpringDataType
import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import io.github.wickedev.graphql.types.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.data.domain.Sort
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface GraphQLDataLoaderForwardConnectionsRepositoryMixin<T : Node> :
    GraphQLDataLoaderForwardConnectionsRepository<T>,
    PropertyRepository<T, ID>, R2dbcRepositoryMixin<T, ID> {

    override fun connection(forward: Forward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {
        val key =
            "${information.repositoryInterface.canonicalName}.findAllForwardConnectById(Forward,DataFetchingEnvironment)"
        return env.dataLoader<Forward, Connection<T>>(key) { keys ->
            keys.map { forwardPagination(it.first ?: DEFAULT_CONNECTION_SIZE, it.after, it.orderBy, null).await() }
        }.load(forward)
    }

    override fun connection(
        forward: Forward,
        criteria: Criteria,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>> {
        val key =
            "${information.repositoryInterface.canonicalName}.findAllForwardConnectById(Forward,Criteria($criteria),DataFetchingEnvironment)"
        return env.dataLoader<Forward, Connection<T>>(key) { keys ->
            keys.map { forwardPagination(it.first ?: DEFAULT_CONNECTION_SIZE, it.after, it.orderBy, criteria).await() }
        }.load(forward)
    }

    private fun forwardPagination(
        first: Int,
        after: ID?,
        orderBy: List<Order>,
        criteria: Criteria?
    ): Mono<Connection<T>> = mono {
        val idOrderInParam = orderBy.find { it.property == getIdProperty().name }?.toSpringDataType()
        val defaultIdOrder = Sort.Order.asc(getIdProperty().name)
        val idOrder: Sort.Order = idOrderInParam ?: defaultIdOrder

        val sort = orderBy.toSpringDataType(if (idOrderInParam == null) defaultIdOrder else null)
        val start = findFirst(sort).awaitSingleOrNull()
        val baseQuery = if (after == null || after.value.isEmpty())
            query(criteria)
        else if (idOrder.direction.isAscending)
            whereIdGreaterThanQuery(after, criteria)
        else
            whereIdLessThanQuery(after, criteria)

        val query = baseQuery
            .limit(first + 1)
            .sort(sort)

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

package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderBackwardConnectionsRepository
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
interface GraphQLDataLoaderBackwardConnectionsRepositoryMixin<T : Node> :
    GraphQLDataLoaderBackwardConnectionsRepository<T>,
    PropertyRepository<T, ID>, R2dbcRepositoryMixin<T, ID> {

    override fun connection(backward: Backward, env: DataFetchingEnvironment): CompletableFuture<Connection<T>> {
        val key =
            "${information.repositoryInterface.canonicalName}.findAllBackwardConnectById(Backward,DataFetchingEnvironment)"
        return env.dataLoader<Backward, Connection<T>>(key) { keys ->
            keys.map { backwardPagination(it.last ?: DEFAULT_CONNECTION_SIZE, it.before, it.orderBy, null).await() }
        }.load(backward)
    }

    override fun connection(
        backward: Backward,
        criteria: Criteria,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>> {
        val key =
            "${information.repositoryInterface.canonicalName}.findAllBackwardConnectById(Backward,Criteria($criteria),DataFetchingEnvironment)"
        return env.dataLoader<Backward, Connection<T>>(key) { keys ->
            keys.map { backwardPagination(it.last ?: DEFAULT_CONNECTION_SIZE, it.before, it.orderBy, criteria).await() }
        }.load(backward)
    }

    private fun backwardPagination(
        last: Int,
        before: ID?,
        orderBy: List<Order>,
        criteria: Criteria?
    ): Mono<Connection<T>> = mono {
        val idOrderInParam = orderBy.find { it.property == getIdProperty().name }?.toSpringDataType()
        val defaultIdOrder = Sort.Order.asc(getIdProperty().name)
        val idOrder: Sort.Order = idOrderInParam ?: defaultIdOrder

        val sort = orderBy.toSpringDataType(if (idOrderInParam == null) defaultIdOrder else null)
        val start = findFirst(sort).awaitSingleOrNull()
        val baseQuery = if (before == null || before.value.isEmpty())
            query(criteria)
        else if (idOrder.direction.isAscending)
            whereIdGreaterThanQuery(before, criteria)
        else
            whereIdLessThanQuery(before, criteria)

        val query = baseQuery
            .limit(last + 1)
            .sort(sort)

        val items = entityOperations.select(query, entity.javaType).await()

        val edges = items.take(last).reversed()

        Connection(
            edges = edges.map { Edge(it, it.id) }, pageInfo = PageInfo(
                hasPreviousPage = items.size > last,
                hasNextPage = edges.lastOrNull()?.id != start?.id,
                startCursor = edges.firstOrNull()?.id?.encoded ?: "",
                endCursor = edges.lastOrNull()?.id?.encoded ?: "",
            )
        )
    }
}

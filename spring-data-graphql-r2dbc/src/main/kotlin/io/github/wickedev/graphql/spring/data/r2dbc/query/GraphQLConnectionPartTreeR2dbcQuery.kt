@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.query

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.spring.data.r2dbc.extentions.dataLoader
import io.github.wickedev.graphql.types.*
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.repository.query.PartTreeR2dbcQuery
import org.springframework.data.repository.query.QueryMethod
import org.springframework.data.repository.query.RepositoryQuery
import org.springframework.data.repository.query.parser.PartTree
import reactor.core.publisher.Flux

class GraphQLConnectionPartTreeR2dbcQuery(
    private val method: QueryMethod,
    private val findAllByQuery: PartTreeR2dbcQuery,
    private val findAllByIdNextQuery: PartTreeR2dbcQuery,
) : RepositoryQuery {

    override fun execute(parameters: Array<out Any?>): Any {
        val dataLoaderKey = method.toString()
        val env: DataFetchingEnvironment =
            parameters.find { it is DataFetchingEnvironment } as? DataFetchingEnvironment
                ?: throw Error("No DataFetchingEnvironment parameter candidate on ${method.name}.")
        val filteredParam = parameters.filter { it !is DataFetchingEnvironment }.toTypedArray()

        return env.dataLoader<Array<Any?>, Connection<*>>(dataLoaderKey) { keys ->
            keys.map { key ->
                val param = key.find { it is ConnectionParam }

                val tree =
                    PartTree(findAllByQuery.queryMethod.name, findAllByQuery.queryMethod.entityInformation.javaType)
                val segment = tree.parts.map { it.property.segment }
                    .toList()
                    .toTypedArray()

                val conditions = key.filter { it !is ConnectionParam }.toTypedArray()

                if (param is Backward) {
                    return@map connection(param, conditions, segment)
                }

                if (param is Forward) {
                    return@map connection(param, conditions, segment)
                }

                throw Error("Cannot found connection param such as last, first, before, after")
            }
        }.load(filteredParam)
    }

    override fun getQueryMethod(): QueryMethod = method

    private suspend fun connection(backward: Backward, conditions: Array<Any?>, segment: Array<String>): Connection<*> {
        val last = backward.last ?: DEFAULT_CONNECTION_SIZE
        val before = backward.before

        val start = if (before == null) findAllBy(
            PageRequest.of(0, 1, Sort.by("id", *segment).descending()), conditions
        ).awaitFirstOrNull()
        else null

        val items = if (before == null) findAllBy(
            PageRequest.of(0, last + 1, Sort.by("id", *segment).descending()), conditions
        ).await()
        else findAllByNext(
            before, PageRequest.of(0, last + 1, Sort.by("id", *segment).descending()), conditions
        ).await()

        val edges = items.take(last)

        return Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) }, pageInfo = PageInfo(
                hasPreviousPage = items.size > last,
                hasNextPage = edges.firstOrNull()?.id != start?.id,
                startCursor = edges.lastOrNull()?.id?.value ?: "",
                endCursor = edges.firstOrNull()?.id?.value ?: "",
            )
        )
    }

    private suspend fun connection(forward: Forward, conditions: Array<Any?>, segment: Array<String>): Connection<*> {
        val first = forward.first ?: DEFAULT_CONNECTION_SIZE
        val after = forward.after

        val start = if (after == null) findAllBy(
            PageRequest.of(0, 1, Sort.by("id", *segment).ascending()), conditions
        ).awaitFirstOrNull()
        else null

        val items = if (after == null) findAllBy(
            PageRequest.of(0, first + 1, Sort.by("id", *segment).ascending()), conditions
        ).await()
        else findAllByNext(
            after, PageRequest.of(0, first + 1, Sort.by("id", *segment).ascending()), conditions
        ).await()


        val edges = items.take(first)

        return Connection(
            edges = edges.map { Edge(it, ConnectionCursor(it.id.value)) }, pageInfo = PageInfo(
                hasPreviousPage = edges.firstOrNull()?.id != start?.id,
                hasNextPage = items.size > first,
                startCursor = edges.firstOrNull()?.id?.value ?: "",
                endCursor = edges.lastOrNull()?.id?.value ?: "",
            )
        )
    }

    private fun findAllBy(pageable: Pageable, conditions: Array<Any?>): Flux<Node> {
        @Suppress("UNCHECKED_CAST")
        return findAllByQuery.execute(arrayOf(*conditions, pageable)) as Flux<Node>
    }

    private fun findAllByNext(id: ID, pageable: Pageable, conditions: Array<Any?>): Flux<Node> {
        @Suppress("UNCHECKED_CAST")
        return findAllByIdNextQuery.execute(arrayOf(id, * conditions, pageable)) as Flux<Node>
    }
}

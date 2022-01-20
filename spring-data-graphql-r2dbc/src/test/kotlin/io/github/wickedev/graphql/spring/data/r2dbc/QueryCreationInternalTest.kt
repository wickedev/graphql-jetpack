package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.fixture
import io.github.wickedev.graphql.types.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux


interface IdOnly {
    val id: ID
}

@Repository
interface QueryCreationInternalTestRepository : GraphQLR2dbcRepository<Post> {

    fun findAllByUserId(userId: ID, pageable: Pageable): Flux<Post>

    fun findAllByIdLessThanAndUserId(id: ID, userId: ID, pageable: Pageable): Flux<Post>

    fun findAllByIdGreaterThanAndUserId(id: ID, userId: ID, pageable: Pageable): Flux<Post>
}

@ContextConfiguration(classes = [TestingApp::class])
class QueryCreationInternalTest(
    private val postRepository: QueryCreationInternalTestRepository,
) : DescribeSpec({
    val userId = ID("1")

    describe("query creation internal") {

        beforeSpec {
            postRepository.saveAll((1..100).map { fixture<Post>().copy(userId = userId) }).await()
        }

        it("backward query") {
            val expect = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).await()
            expect.size shouldBe 100

            val last = 10
            var before: ID? = null

            (0..9).forEach { page ->
                val start = if (before == null) postRepository.findAllByUserId(
                    userId, PageRequest.of(0, 1, Sort.by("id", "userId").descending())
                ).awaitFirstOrNull()
                else null

                val items = if (before == null) postRepository.findAllByUserId(
                    userId, PageRequest.of(0, last + 1, Sort.by("id", "userId").descending())
                ).await()
                else postRepository.findAllByIdLessThanAndUserId(
                    before!!, userId, PageRequest.of(0, last + 1, Sort.by("id", "userId").descending())
                ).await()

                items.map { it.title }.take(last) shouldBe expect.drop(page * 10).take(10).map { it.title }

                val edges = items.take(last)

                before = edges.last().id

                val connection = Connection(
                    edges = edges.map { Edge(it, it.id) }, pageInfo = PageInfo(
                        hasPreviousPage = items.size > last,
                        hasNextPage = edges.firstOrNull()?.id != start?.id,
                        startCursor = edges.lastOrNull()?.id?.value ?: "",
                        endCursor = edges.firstOrNull()?.id?.value ?: "",
                    )
                )

                connection.pageInfo.startCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 9)
                connection.pageInfo.hasNextPage shouldBe (page != 0)

                connection.edges.map { it.node.title } shouldBe expect.drop(page * 10).take(10).map { it.title }
            }
        }

        it("forward query") {
            val expect = postRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).await()
            expect.size shouldBe 100

            val first = 10
            var after: ID? = null

            (0..9).forEach { page ->
                val start = if (after == null) postRepository.findAllByUserId(
                    userId,
                    PageRequest.of(0, 1, Sort.by("id", "userId").ascending())
                ).awaitFirstOrNull()
                else null

                val items = if (after == null) postRepository.findAllByUserId(
                    userId, PageRequest.of(0, first + 1, Sort.by("id", "userId").ascending())
                ).await()
                else postRepository.findAllByIdGreaterThanAndUserId(
                    after!!, userId, PageRequest.of(0, first + 1, Sort.by("id", "userId").ascending())
                ).await()


                items.map { it.title }.take(first) shouldBe expect.drop(page * 10).take(10).map { it.title }

                val edges = items.take(first)

                after = edges.last().id

                val connection = Connection(
                    edges = edges.map { Edge(it, it.id) }, pageInfo = PageInfo(
                        hasPreviousPage = edges.firstOrNull()?.id != start?.id,
                        hasNextPage = items.size > first,
                        startCursor = edges.firstOrNull()?.id?.value ?: "",
                        endCursor = edges.lastOrNull()?.id?.value ?: "",
                    )
                )

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 0)
                connection.pageInfo.hasNextPage shouldBe (page != 9)

                connection.edges.map { it.node.title } shouldBe expect.drop(page * 10).take(10).map { it.title }
            }
        }
    }
})

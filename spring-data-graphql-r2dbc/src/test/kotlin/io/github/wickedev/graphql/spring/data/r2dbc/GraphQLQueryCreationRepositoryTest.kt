package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchThenAwait
import io.github.wickedev.graphql.spring.data.r2dbc.utils.fixture
import io.github.wickedev.graphql.types.Backward
import io.github.wickedev.graphql.types.Connection
import io.github.wickedev.graphql.types.Forward
import io.github.wickedev.graphql.types.ID
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.dataloader.DataLoaderRegistry
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration
import java.util.concurrent.CompletableFuture


@Repository
interface QueryCreationPostRepository : GraphQLR2dbcRepository<Post> {
    fun findByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Post>

    fun findAllByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<List<Post>>

    fun existsByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean>

    fun countByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Long>

    fun connectionByUserId(
        userId: ID,
        backward: Backward,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<Post>>

    fun connectionByUserId(
        userId: ID,
        forward: Forward,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<Post>>
}

@ContextConfiguration(classes = [TestingApp::class])
class GraphQLQueryCreationRepositoryTest(
    private val postRepository: QueryCreationPostRepository,
) : DescribeSpec({
    val userId = ID("1")
    lateinit var saved: List<Post>

    beforeSpec {
        saved = postRepository.saveAll((1..100).map { fixture<Post>().copy(userId = userId) }).await()
    }

    describe("query creation repository") {
        it("findByUserId should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val post = postRepository.findByUserId(userId, env).dispatchThenAwait(env)

            post.userId shouldBe saved.first().userId
        }

        it("findAllByUserId should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val posts = postRepository.findAllByUserId(userId, env).dispatchThenAwait(env)

            posts shouldBe saved
        }

        it("existsByUserId should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val exist = postRepository.existsByUserId(userId, env).dispatchThenAwait(env)

            exist shouldBe true
        }

        it("countByUserId should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val count = postRepository.countByUserId(userId, env).dispatchThenAwait(env)

            count shouldBe 100
        }

        it("backward connections should be iterate all edges") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).await()
            expect.size shouldBe 100

            val last = 10
            var before: ID? = null

            (0..9).forEach { page ->
                val connection =
                    postRepository.connectionByUserId(userId, Backward(last, before), env).dispatchThenAwait(env)

                before = ID(connection.pageInfo.startCursor)

                connection.pageInfo.startCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 9)
                connection.pageInfo.hasNextPage shouldBe (page != 0)

                connection.edges.map { it.node.title } shouldBe expect.drop(page * 10).take(10).map { it.title }
            }
        }

        it("forward connections should be iterate all edges") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = postRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).await()
            expect.size shouldBe 100

            val first = 10
            var after: ID? = null

            (0..9).forEach { page ->
                val connection =
                    postRepository.connectionByUserId(userId, Forward(first, after), env).dispatchThenAwait(env)

                after = ID(connection.pageInfo.endCursor)

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 0)
                connection.pageInfo.hasNextPage shouldBe (page != 9)

                connection.edges.map { it.node.title } shouldBe expect.drop(page * 10).take(10).map { it.title }
            }
        }
    }
})

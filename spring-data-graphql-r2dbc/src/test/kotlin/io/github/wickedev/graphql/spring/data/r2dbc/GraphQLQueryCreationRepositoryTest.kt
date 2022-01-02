package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchThenAwait
import io.github.wickedev.graphql.spring.data.r2dbc.utils.fixture
import io.github.wickedev.graphql.types.ID
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.dataloader.DataLoaderRegistry
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration
import java.util.concurrent.CompletableFuture


@Repository
interface QueryCreationPostRepository : GraphQLR2dbcRepository<Post> {
    fun findByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Post>

    fun findAllByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<List<Post>>

    fun existsByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean>

    fun countByUserId(id: ID, env: DataFetchingEnvironment): CompletableFuture<Long>
}

@ContextConfiguration(classes = [TestingApp::class])
class GraphQLQueryCreationRepositoryTest(
    private val postRepository: QueryCreationPostRepository,
) : DescribeSpec({
    val userId = ID("1")
    lateinit var saved: List<Post>

    beforeSpec {
        saved = postRepository.saveAll((0..9).map { fixture<Post>().copy(userId = userId) }).await()
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

            count shouldBe saved.size
        }
    }
})
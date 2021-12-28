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
    fun findAllByUserId(id: ID, env:DataFetchingEnvironment): CompletableFuture<List<Post>>
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
        it("findAllByUserId should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val posts = postRepository.findAllByUserId(userId, env).dispatchThenAwait(env)

            posts shouldBe saved
        }
    }
})
package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.graphql.extentions.mono.await
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchThenAwait
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dataloader.DataLoaderRegistry
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [TestingApp::class])
class GraphQLDataLoaderNodeRepositoryTest(
    private val userRepository: UserRepository,
    private val nodeRepository: GraphQLNodeRepository,
) : DescribeSpec({
    lateinit var saved: User

    beforeSpec {
        saved = userRepository.save(fixture<User>()).await()
    }

    describe("graphql node repository") {
        it("should be return node entity") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val user = nodeRepository.findNodeById(saved.id, env).dispatchThenAwait(env)

            user shouldNotBe null
            user shouldBe saved
        }
    }
})
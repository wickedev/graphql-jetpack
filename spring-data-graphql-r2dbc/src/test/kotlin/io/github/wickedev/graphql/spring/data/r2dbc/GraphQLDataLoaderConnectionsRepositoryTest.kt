package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.extentions.flux.await
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.sut.TestingApp
import io.github.wickedev.graphql.spring.data.r2dbc.sut.User
import io.github.wickedev.graphql.spring.data.r2dbc.sut.UserRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchThenAwait
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.dataloader.DataLoaderRegistry
import org.springframework.data.domain.Sort
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [TestingApp::class])
class GraphQLDataLoaderConnectionsRepositoryTest(
    private val userRepository: UserRepository,
) : DescribeSpec({
    beforeSpec {
        userRepository.saveAll((1..100).map { fixture<User>() }).await()
    }

    describe("graphql repository backward connections") {
        it("should be iterate all edges") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).await()
            expect.size shouldBe 100

            val last = 10
            var before: ID? = null

            (0..9).forEach { page ->
                val connection = userRepository.findAllBackwardConnectById(last, before, env).dispatchThenAwait(env)

                before = ID(connection.pageInfo.startCursor)

                connection.pageInfo.startCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 9)
                connection.pageInfo.hasNextPage shouldBe (page != 0)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).map { it.name }
            }
        }
    }

    describe("graphql repository forward connections") {
        it("should be iterate all edges") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).await()
            expect.size shouldBe 100

            val first = 10
            var after: ID? = null

            (0..9).forEach { page ->
                val connection = userRepository.findAllForwardConnectById(first, after, env).dispatchThenAwait(env)

                after = ID(connection.pageInfo.endCursor)

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.value
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.value
                connection.pageInfo.hasPreviousPage shouldBe (page != 0)
                connection.pageInfo.hasNextPage shouldBe (page != 9)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).map { it.name }
            }
        }
    }
})
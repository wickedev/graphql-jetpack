package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.coroutine.reactive.extensions.flux.await
import io.github.wickedev.graphql.extentions.toLocalID
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchThenAwait
import io.github.wickedev.graphql.spring.data.r2dbc.utils.fixture
import io.github.wickedev.graphql.types.*
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
                val connection = userRepository.connection(Forward(first, after), env).dispatchThenAwait(env)

                after = connection.pageInfo.endCursor.toLocalID()

                println(connection.pageInfo)

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.encoded
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.encoded
                connection.pageInfo.hasPreviousPage shouldBe (page != 0)
                connection.pageInfo.hasNextPage shouldBe (page != 9)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).map { it.name }
            }
        }

        it("should be iterate all edges order by desc") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).await()
            expect.size shouldBe 100

            val first = 10
            var after: ID? = null

            (0..9).forEach { page ->
                val connection = userRepository.connection(
                    Forward(first, after, Sort(listOf(Order("id", Direction.DESC)))),
                    env
                ).dispatchThenAwait(env)

                after = connection.pageInfo.endCursor.toLocalID()

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.encoded
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.encoded
                connection.pageInfo.hasPreviousPage shouldBe (page != 0)
                connection.pageInfo.hasNextPage shouldBe (page != 9)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).map { it.name }
            }
        }
    }

    describe("graphql repository backward connections") {
        it("should be iterate all edges") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).await()
            expect.size shouldBe 100

            val last = 10
            var before: ID? = null

            (0..9).forEach { page ->
                val connection = userRepository.connection(Backward(last, before), env).dispatchThenAwait(env)

                before = connection.pageInfo.startCursor.toLocalID()

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.encoded
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.encoded
                connection.pageInfo.hasPreviousPage shouldBe (page != 9)
                connection.pageInfo.hasNextPage shouldBe (page != 0)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).reversed()
                    .map { it.name }
            }
        }

        it("should be iterate all edges order by desc") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val expect = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).await()
            expect.size shouldBe 100

            val last = 10
            var before: ID? = null

            (0..9).forEach { page ->
                val connection =
                    userRepository.connection(
                        Backward(last, before, Sort(listOf(Order("id", Direction.DESC)))),
                        env
                    )
                        .dispatchThenAwait(env)

                before = connection.pageInfo.startCursor.toLocalID()

                connection.pageInfo.startCursor shouldBe connection.edges.first().cursor.encoded
                connection.pageInfo.endCursor shouldBe connection.edges.last().cursor.encoded
                connection.pageInfo.hasPreviousPage shouldBe (page != 9)
                connection.pageInfo.hasNextPage shouldBe (page != 0)

                connection.edges.map { it.node.name } shouldBe expect.drop(page * 10).take(10).reversed()
                    .map { it.name }
            }
        }
    }
})
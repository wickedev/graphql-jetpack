package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.extentions.mono.await
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.sut.TestingApp
import io.github.wickedev.graphql.spring.data.r2dbc.sut.User
import io.github.wickedev.graphql.spring.data.r2dbc.sut.UserRepository
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import org.dataloader.DataLoaderRegistry
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [TestingApp::class])
class GraphQLDataLoaderRepositoryTest(
    private val userRepository: UserRepository,
) : DescribeSpec({
    lateinit var saved: User

    beforeSpec {
        saved = userRepository.save(fixture<User>()).await()
    }

    describe("graphql data loader repository") {
        it("findById should be correct") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val existsFuture = userRepository.findById(saved.id, env)
            val nonExistsId = "290518"
            val nonExistsFuture = userRepository.findById(ID(nonExistsId), env)

            env.dispatchAll()

            existsFuture.await() shouldBe saved
            nonExistsFuture.await() shouldBe null
        }

        it("existsById should be return true") {
            val env = newDataFetchingEnvironment()
                .dataLoaderRegistry(DataLoaderRegistry())
                .build()

            val existsFuture = userRepository.existsById(saved.id, env)
            val nonExistsId = "290518"
            val nonExistsFuture = userRepository.existsById(ID(nonExistsId), env)

            env.dispatchAll()

            existsFuture.await() shouldBe true
            nonExistsFuture.await() shouldBe false
        }
    }
})
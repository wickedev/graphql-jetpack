package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import io.github.wickedev.graphql.extentions.mono.await
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.utils.dispatchAll
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import org.dataloader.DataLoaderRegistry
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [TestingApp::class])
class GraphQLDataLoaderRepositoryFindAllTest(
    private val userRepository: UserRepository,
) : DescribeSpec({
    lateinit var saved: User

    beforeSpec {
        saved = userRepository.save(fixture<User>()).await()
    }

    describe("graphql data loader repository") {
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
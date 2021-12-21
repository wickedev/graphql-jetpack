package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.spring.data.r2dbc.utils.fixture
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestingApp::class])
class RepositoryTest(
    private val userRepository: UserRepository
) : DescribeSpec({

    describe("user repository") {
        it("should be insert entity to database") {
            val user = userRepository.save(fixture()).await()
            user shouldNotBe null
        }
    }
})
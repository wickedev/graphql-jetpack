package io.github.wickedev.graphql.spring.data.r2dbc

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestingApp::class])
class SubRepositoryTest(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : DescribeSpec({

    describe("user sub repository") {
        it("should not be null") {
            userRepository shouldNotBe null
            postRepository shouldNotBe null
        }
    }
})
package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.spring.data.r2dbc.sut.TestingApp
import io.github.wickedev.graphql.spring.data.r2dbc.sut.UserConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.sut.UserDataLoaderRepository
import io.github.wickedev.graphql.spring.data.r2dbc.sut.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TestingApp::class])
class SubRepositoryTest(
    private val userRepository: UserRepository,
    private val userDataLoaderRepository: UserDataLoaderRepository,
    private val userConnectionsRepository: UserConnectionsRepository
) : DescribeSpec({

    describe("user sub repository") {
        it("should not be null") {
            userRepository shouldNotBe null
            userDataLoaderRepository shouldNotBe null
            userConnectionsRepository shouldNotBe null
        }
    }
})
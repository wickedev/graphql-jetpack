package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.extentions.mono.await
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.factory.GraphQLR2dbcRepositoryFactoryBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration

@SpringBootApplication
@EnableR2dbcRepositories(repositoryFactoryBeanClass = GraphQLR2dbcRepositoryFactoryBean::class)
class TestingApp {
    @Bean
    fun connectionFactory(): ConnectionFactory =
        ConnectionFactories.get("r2dbc:tc:postgresql:///test?TC_IMAGE_TAG=9.6.23")

    @Bean
    fun populator(): DatabasePopulator {
        val resourcePaths = listOf("db/schema.sql")
        return CompositeDatabasePopulator(
            resourcePaths.map {
                ResourceDatabasePopulator(
                    ClassPathResource(it)
                )
            }
        )
    }

    @Bean
    fun initializer(
        connectionFactory: ConnectionFactory,
        populator: DatabasePopulator
    ): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}

@Table("users")
data class User(
    @Id val id: ID,
    val name: String
)

@Repository
interface UserRepository : ReactiveCrudRepository<User, ID>

@ContextConfiguration(classes = [TestingApp::class])
class SpringAutowiredTest(
    private val userRepository: UserRepository
) : DescribeSpec({

    describe("user repository") {
        it("should be insert entity to database") {
            val user = userRepository.save(fixture()).await()

            user shouldNotBe null
        }
    }
})
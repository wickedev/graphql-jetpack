package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.spring.data.r2dbc.configuration.EnableGraphQLR2dbcRepositories
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.DatabasePopulator
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@SpringBootApplication
@EnableGraphQLR2dbcRepositories
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

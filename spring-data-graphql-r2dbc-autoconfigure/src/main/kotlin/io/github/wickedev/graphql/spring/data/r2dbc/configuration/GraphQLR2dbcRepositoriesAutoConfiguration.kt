package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.graphql.repository.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.factory.GraphQLR2dbcRepositoryFactoryBean
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.r2dbc.core.DatabaseClient


@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(
    ConnectionFactory::class,
    GraphQLR2dbcRepository::class,
    GraphQLNodeRepository::class,
)
@ConditionalOnBean(DatabaseClient::class)
@ConditionalOnProperty(
    prefix = "spring.data.graphql.r2dbc.repositories",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@ConditionalOnMissingBean(GraphQLR2dbcRepositoryFactoryBean::class)
@Import(GraphQLR2dbcRepositoriesAutoConfigureRegistrar::class)
@AutoConfigureAfter(GraphQLR2dbcAutoConfiguration::class)
class GraphQLR2dbcRepositoriesAutoConfiguration
package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

class GraphQLR2dbcRepositoriesAutoConfigureRegistrar: AbstractRepositoryConfigurationSourceSupport() {

    override fun getAnnotation(): Class<out Annotation?> {
        return EnableGraphQLR2dbcRepositories::class.java
    }

    override fun getConfiguration(): Class<*> {
        return EnableGraphQLR2dbcRepositoriesConfiguration::class.java
    }

    override fun getRepositoryConfigurationExtension(): RepositoryConfigurationExtension {
        return GraphQLR2dbcRepositoryConfigurationExtension()
    }

    @EnableGraphQLR2dbcRepositories
    class EnableGraphQLR2dbcRepositoriesConfiguration
}

package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

internal class GraphQLR2dbcRepositoriesRegistrar : RepositoryBeanDefinitionRegistrarSupport() {

    override fun getAnnotation(): Class<out Annotation?> {
        return EnableGraphQLR2dbcRepositories::class.java
    }

    override fun getExtension(): RepositoryConfigurationExtension {
        return GraphQLR2dbcRepositoryConfigurationExtension()
    }
}

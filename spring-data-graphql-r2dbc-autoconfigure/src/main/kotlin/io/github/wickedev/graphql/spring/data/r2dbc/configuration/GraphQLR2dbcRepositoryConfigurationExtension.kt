package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.spring.data.r2dbc.factory.GraphQLR2dbcRepositoryFactoryBean
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import org.springframework.data.r2dbc.repository.config.R2dbcRepositoryConfigurationExtension
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.core.RepositoryMetadata
import java.util.concurrent.CompletableFuture

class GraphQLR2dbcRepositoryConfigurationExtension : R2dbcRepositoryConfigurationExtension() {

    override fun getModuleName(): String {
        return "GRAPHQL-R2DBC"
    }

    override fun getModulePrefix(): String {
        return "graphql-r2dbc"
    }

    override fun getRepositoryFactoryBeanClassName(): String {
        return GraphQLR2dbcRepositoryFactoryBean::class.java.name
    }

    override fun getIdentifyingAnnotations(): Collection<Class<out Annotation?>> {
        return setOf<Class<out Annotation?>>(Table::class.java)
    }

    override fun getIdentifyingTypes(): Collection<Class<*>> {
        return setOf<Class<*>>(GraphQLR2dbcRepository::class.java)
    }

    override fun useRepositoryConfiguration(metadata: RepositoryMetadata): Boolean {
        return metadata.isReactiveRepository || metadata.isGraphQLRepository
    }

    private val RepositoryMetadata.isGraphQLRepository: Boolean
        get() {
            return repositoryInterface.methods
                .map { it.parameterTypes to it.returnType }
                .any {
                    val hasDfeParam = it.first.any { paramType -> paramType == DataFetchingEnvironment::class.java }
                    val returnTypeIsCompletableFuture = it.second == CompletableFuture::class.java
                    hasDfeParam && returnTypeIsCompletableFuture
                }
        }
}
package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.graphql.spring.data.r2dbc.factory.GraphQLR2dbcRepositoryFactoryBean
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLRepository
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.util.StringUtils

class GraphQLR2dbcRepositoryConfigurationExtension : RepositoryConfigurationExtensionSupport() {

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

    override fun postProcess(builder: BeanDefinitionBuilder, config: XmlRepositoryConfigurationSource) {}

    override fun postProcess(builder: BeanDefinitionBuilder, config: AnnotationRepositoryConfigurationSource) {
        val attributes = config.attributes
        val databaseClientRef = attributes.getString("databaseClientRef")
        if (StringUtils.hasText(databaseClientRef)) {
            builder.addPropertyReference("databaseClient", attributes.getString("databaseClientRef"))
            builder.addPropertyReference("dataAccessStrategy", "reactiveDataAccessStrategy")
        } else {
            builder.addPropertyReference("entityOperations", attributes.getString("entityOperationsRef"))
        }
    }

    override fun useRepositoryConfiguration(metadata: RepositoryMetadata): Boolean {
        return metadata.isReactiveRepository
    }
}
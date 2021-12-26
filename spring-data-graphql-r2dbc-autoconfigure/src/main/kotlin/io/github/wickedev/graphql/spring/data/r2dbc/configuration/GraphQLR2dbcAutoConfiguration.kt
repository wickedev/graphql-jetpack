package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.graphql.repository.GraphQLDataLoaderByIdRepository
import io.github.wickedev.graphql.repository.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.converter.GraphQLMappingR2dbcConverter
import io.github.wickedev.graphql.spring.data.r2dbc.converter.IDToLongWritingConverter
import io.github.wickedev.graphql.spring.data.r2dbc.mapping.GraphQLR2dbcMappingContext
import io.github.wickedev.graphql.spring.data.r2dbc.repository.SimpleGraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.DefaultIDTypeStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.GraphQLAdditionalIsNewStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeStrategy
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.dialect.R2dbcDialect
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.r2dbc.core.DatabaseClient

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DatabaseClient::class, R2dbcEntityTemplate::class)
@AutoConfigureAfter(R2dbcAutoConfiguration::class)
class GraphQLR2dbcAutoConfiguration(private val databaseClient: DatabaseClient) {

    val dialect: R2dbcDialect = DialectResolver.getDialect(this.databaseClient.connectionFactory)

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcEntityTemplate(r2dbcConverter: R2dbcConverter): R2dbcEntityTemplate? {
        return R2dbcEntityTemplate(databaseClient, dialect, r2dbcConverter)
    }

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcMappingContext(
        namingStrategy: ObjectProvider<NamingStrategy>,
        r2dbcCustomConversions: R2dbcCustomConversions,
        idTypeStrategy: IDTypeStrategy,
    ): R2dbcMappingContext {
        val relationalMappingContext = GraphQLR2dbcMappingContext(
            namingStrategy.getIfAvailable { NamingStrategy.INSTANCE }, idTypeStrategy
        )
        relationalMappingContext.setSimpleTypeHolder(r2dbcCustomConversions.simpleTypeHolder)
        return relationalMappingContext
    }

    @Bean
    @ConditionalOnMissingBean
    fun additionalIsNewStrategy(): AdditionalIsNewStrategy = GraphQLAdditionalIsNewStrategy()

    @Bean
    @ConditionalOnMissingBean
    fun idTypeStrategy(): IDTypeStrategy = DefaultIDTypeStrategy()

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcConverter(
        context: R2dbcMappingContext,
        conversions: CustomConversions,
        additionalIsNewStrategy: AdditionalIsNewStrategy,
    ): MappingR2dbcConverter {
        return GraphQLMappingR2dbcConverter(context, conversions, additionalIsNewStrategy)
    }

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcCustomConversions(): R2dbcCustomConversions? {
        val converters: MutableList<Any> = ArrayList(dialect.converters)
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS)
        return R2dbcCustomConversions(
            CustomConversions.StoreConversions.of(dialect.simpleTypeHolder, converters),
            listOf(
                IDToLongWritingConverter(),
                // LongToIDReadingConverter(),
            )
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun graphQLNodeRepository(
        repositories: ObjectProvider<GraphQLDataLoaderByIdRepository<*>>,
        idTypeStrategy: IDTypeStrategy,
    ): GraphQLNodeRepository {
        return SimpleGraphQLNodeRepository(repositories, idTypeStrategy)
    }
}

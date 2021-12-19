package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.extentions.copyWithId
import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.spring.data.r2dbc.converter.GraphQLMappingR2dbcConverter
import io.github.wickedev.graphql.spring.data.r2dbc.converter.IDToLongWritingConverter
import io.github.wickedev.graphql.spring.data.r2dbc.converter.LongToIDReadingConverter
import io.github.wickedev.graphql.spring.data.r2dbc.repository.SimpleGraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.GraphQLAdditionalIsNewStrategy
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback
import reactor.core.publisher.Mono

@Configuration
class GraphQLSpringDataR2dbcConfiguration {
    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(connectionFactory)
        return R2dbcCustomConversions.of(
            dialect,
            IDToLongWritingConverter(),
            LongToIDReadingConverter(),
        )
    }

    @Bean
    fun additionalIsNewStrategy(): AdditionalIsNewStrategy {
        return GraphQLAdditionalIsNewStrategy()
    }

    @Bean
    fun mappingR2dbcConverter(
        context: R2dbcMappingContext,
        conversions: CustomConversions,
        additionalIsNewStrategy: AdditionalIsNewStrategy,
    ): MappingR2dbcConverter {
        return GraphQLMappingR2dbcConverter(context, conversions, additionalIsNewStrategy)
    }

    @Bean
    fun graphQLNodeRepository(repositories: ObjectProvider<GraphQLDataLoaderRepository<*>>): GraphQLNodeRepository {
        return SimpleGraphQLNodeRepository(repositories)
    }

    @Bean
    fun afterConvertCallback(): AfterConvertCallback<Node> {
        return AfterConvertCallback<Node> { entity, table ->
            val node = if (entity.id.type.isEmpty())
                copyWithId(entity, entity.id.toGlobalId(table.reference))
            else
                entity

            Mono.just(node)
        }
    }
}

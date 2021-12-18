package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.graphql.spring.data.r2dbc.converter.GraphQLMappingR2dbcConverter
import io.github.wickedev.graphql.spring.data.r2dbc.converter.IDToLongWritingConverter
import io.github.wickedev.graphql.spring.data.r2dbc.converter.LongToIDReadingConverter
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.GraphQLAdditionalIsNewStrategy
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.convert.CustomConversions
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext

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

    /*@Bean
    fun mappingR2dbcConverter(
        context: R2dbcMappingContext,
        conversions: CustomConversions,
        additionalIsNewStrategy: AdditionalIsNewStrategy,
    ): MappingR2dbcConverter {
        return GraphQLMappingR2dbcConverter(context, conversions, additionalIsNewStrategy)
    }*/
}
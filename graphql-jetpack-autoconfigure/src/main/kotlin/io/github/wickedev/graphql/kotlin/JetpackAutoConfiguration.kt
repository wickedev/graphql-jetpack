package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.server.spring.GraphQLAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    JetpackGraphQLSchemaConfiguration::class,
    GraphQLAutoConfiguration::class,
)
@AutoConfigureBefore(GraphQLAutoConfiguration::class)
class JetpackAutoConfiguration
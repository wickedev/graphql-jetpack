package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.server.spring.NonFederatedSchemaAutoConfiguration
import io.github.wickedev.graphql.AuthSchemaDirectiveWiring
import io.github.wickedev.graphql.scalars.CustomScalars
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "false", matchIfMissing = true)
@Import(JetpackGraphQLExecutionConfiguration::class)
@AutoConfigureBefore(NonFederatedSchemaAutoConfiguration::class)
class JetpackNonFederatedSchemaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun schemaGeneratorHooks(
        customScalars: CustomScalars,
        authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
    ): JetpackSchemaGeneratorHooks =
        JetpackSchemaGeneratorHooks(
            customScalars,
            authSchemaDirectiveWiring
        )
}
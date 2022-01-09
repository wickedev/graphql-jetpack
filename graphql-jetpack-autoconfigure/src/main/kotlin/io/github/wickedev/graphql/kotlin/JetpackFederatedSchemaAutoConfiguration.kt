package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.server.spring.FederatedSchemaAutoConfiguration
import io.github.wickedev.graphql.AuthSchemaDirectiveWiring
import io.github.wickedev.graphql.scalars.CustomScalars
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.util.*

@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "true")
@Import(JetpackGraphQLExecutionConfiguration::class)
@AutoConfigureBefore(FederatedSchemaAutoConfiguration::class)
class JetpackFederatedSchemaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun federatedSchemaGeneratorHooks(
        resolvers: Optional<List<FederatedTypeResolver<*>>>,
        customScalars: CustomScalars,
        authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
    ): FederatedSchemaGeneratorHooks =
        JetpackFederatedSchemaGeneratorHooks(
            resolvers.orElse(emptyList()),
            customScalars,
            authSchemaDirectiveWiring
        )
}
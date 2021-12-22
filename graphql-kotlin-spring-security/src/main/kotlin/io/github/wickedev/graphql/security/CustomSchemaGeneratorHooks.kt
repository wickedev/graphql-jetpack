package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver

class CustomSchemaGeneratorHooks(
    resolvers: List<FederatedTypeResolver<*>>,
    private val authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
) :
    FederatedSchemaGeneratorHooks(resolvers) {
    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = CustomDirectiveWiringFactory(authSchemaDirectiveWiring)
}

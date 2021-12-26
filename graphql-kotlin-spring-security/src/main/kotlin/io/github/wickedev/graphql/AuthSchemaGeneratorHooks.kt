package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import graphql.schema.GraphQLType
import io.github.wickedev.graphql.scalars.CustomScalars
import kotlin.reflect.KType

class AuthSchemaGeneratorHooks(
    resolvers: List<FederatedTypeResolver<*>>,
    private val customScalars: CustomScalars,
    private val authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
) :
    FederatedSchemaGeneratorHooks(resolvers) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (true) {
            customScalars.exists(type.classifier) -> customScalars.typeToGraphQLType(type.classifier)
            else -> super.willGenerateGraphQLType(type)
        }
    }

    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = AuthDirectiveWiringFactory(authSchemaDirectiveWiring)
}

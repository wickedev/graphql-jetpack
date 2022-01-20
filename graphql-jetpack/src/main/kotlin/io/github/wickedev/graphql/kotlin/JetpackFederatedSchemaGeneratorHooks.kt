package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import io.github.wickedev.graphql.AuthDirectiveWiringFactory
import io.github.wickedev.graphql.AuthSchemaDirectiveWiring
import io.github.wickedev.graphql.directives.*
import io.github.wickedev.graphql.scalars.CustomScalars
import org.reactivestreams.Publisher
import kotlin.reflect.KType

open class JetpackFederatedSchemaGeneratorHooks(
    resolvers: List<FederatedTypeResolver<*>>,
    private val customScalars: CustomScalars,
    private val authSchemaDirectiveWiring: AuthSchemaDirectiveWiring,
) : FederatedSchemaGeneratorHooks(resolvers) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (true) {
            customScalars.exists(type.classifier) -> customScalars.typeToGraphQLType(type.classifier)
            else -> super.willGenerateGraphQLType(type)
        }
    }

    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = AuthDirectiveWiringFactory(authSchemaDirectiveWiring)

    override fun willResolveInputMonad(type: KType): KType = when (type.classifier) {
        Publisher::class -> type.arguments.firstOrNull()?.type
        else -> type
    } ?: type
}

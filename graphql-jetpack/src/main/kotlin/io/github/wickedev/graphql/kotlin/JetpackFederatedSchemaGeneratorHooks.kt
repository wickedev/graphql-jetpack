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

    private val relayDirectiveTypes: Set<GraphQLDirective> =
        setOf(
            APPEND_EDGE_DIRECTIVE_TYPE,
            APPEND_NODE_DIRECTIVE_TYPE,
            CONNECTION_DIRECTIVE_TYPE,
            DELETE_EDGE_DIRECTIVE_TYPE,
            DELETE_RECORD_DIRECTIVE_TYPE,
            INLINE_DIRECTIVE_TYPE,
            MATCH_DIRECTIVE_TYPE,
            MODULE_DIRECTIVE_TYPE,
            NO_INLINE_DIRECTIVE_TYPE,
            PRE_LOADABLE_DIRECTIVE_TYPE,
            PREPEND_EDGE_DIRECTIVE_TYPE,
            PREPEND_NODE_DIRECTIVE_TYPE,
            RAW_RESPONSE_TYPE_DIRECTIVE_TYPE,
            REFETCHABLE_DIRECTIVE_TYPE,
            RELAY_DIRECTIVE_TYPE,
            REQUIRED_DIRECTIVE_TYPE,
            STREAM_CONNECTION_DIRECTIVE_TYPE,
        )

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        return when (true) {
            customScalars.exists(type.classifier) -> customScalars.typeToGraphQLType(type.classifier)
            else -> super.willGenerateGraphQLType(type)
        }
    }

    override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder {
        builder.additionalDirectives(relayDirectiveTypes)
        return super.willBuildSchema(builder)
    }

    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = AuthDirectiveWiringFactory(authSchemaDirectiveWiring)

    override fun willResolveInputMonad(type: KType): KType = when (type.classifier) {
        Publisher::class -> type.arguments.firstOrNull()?.type
        else -> type
    } ?: type
}

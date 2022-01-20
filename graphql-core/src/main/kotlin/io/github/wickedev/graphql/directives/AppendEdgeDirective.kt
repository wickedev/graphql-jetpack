package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import io.github.wickedev.graphql.scalars.GraphQLIDScalar

internal const val APPEND_EDGE_DIRECTIVE_NAME = "appendEdge"
private val APPEND_EDGE_DIRECTIVE_DESCRIPTION = """
        (Relay Only)

        For use within mutations. After the mutation request is complete, this edge
        will be prepended to its parent connection.
        
        [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
    """.trimIndent()

val APPEND_EDGE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(APPEND_EDGE_DIRECTIVE_NAME)
    .description(APPEND_EDGE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .argument {
        it.name("connections")
        it.type(nonNull(list(nonNull(GraphQLIDScalar))))
    }
    .build()

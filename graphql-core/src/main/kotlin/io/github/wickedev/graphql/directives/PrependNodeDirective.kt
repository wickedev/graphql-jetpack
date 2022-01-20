package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import io.github.wickedev.graphql.scalars.GraphQLIDScalar

internal const val PREPEND_NODE_DIRECTIVE_NAME = "prependNode"
private val PREPEND_NODE_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
        
        For use within mutations. After the mutation request is complete, this node
        will be prepended to its parent connection.
        
        [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
    """.trimIndent()

val PREPEND_NODE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(PREPEND_NODE_DIRECTIVE_NAME)
    .description(PREPEND_NODE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .argument {
        it.name("connections")
        it.type(nonNull(list(nonNull(GraphQLIDScalar))))
    }
    .argument {
        it.name("edgeTypeName")
        it.type(nonNull(Scalars.GraphQLString))
    }
    .build()

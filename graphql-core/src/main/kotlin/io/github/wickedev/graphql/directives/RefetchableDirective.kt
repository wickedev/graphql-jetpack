package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull

internal const val REFETCHABLE_DIRECTIVE_NAME = "refetchable"
private val REFETCHABLE_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
    
        For use with [`useRefetchableFragment`](https://relay.dev/docs/api-reference/use-refetchable-fragment/).
    
        The @refetchable directive can only be added to fragments that are
        "refetchable", that is, on fragments that are declared on Viewer or Query
        types, or on a type that implements `Node` (i.e. a type that has an id).
    
        [Read More](https://relay.dev/docs/api-reference/use-refetchable-fragment/#arguments)
    """.trimIndent()

val REFETCHABLE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(REFETCHABLE_DIRECTIVE_NAME)
    .description(REFETCHABLE_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("queryName")
        it.type(nonNull(Scalars.GraphQLString))
    }
    .argument {
        it.name("directives")
        it.type(list(nonNull(Scalars.GraphQLString)))
    }
    .validLocations(Introspection.DirectiveLocation.FRAGMENT_DEFINITION)
    .build()

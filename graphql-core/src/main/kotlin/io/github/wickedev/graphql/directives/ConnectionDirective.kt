package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull


internal const val CONNECTION_DIRECTIVE_NAME = "connection"
private val CONNECTION_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
        
        A directive which declares that a field implements the connection spec.
        
        [Read More](https://relay.dev/docs/guided-tour/list-data/pagination/)
    """.trimIndent()

val CONNECTION_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(CONNECTION_DIRECTIVE_NAME)
    .description(CONNECTION_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .argument {
        it.name("key")
        it.type(nonNull(Scalars.GraphQLString))
    }
    .argument {
        it.name("filters")
        it.type(list(Scalars.GraphQLString))
    }
    .argument {
        it.name("handler")
        it.type(Scalars.GraphQLString)
    }
    .argument {
        it.name("dynamicKey_UNSTABLE")
        it.type(Scalars.GraphQLString)
    }
    .build()

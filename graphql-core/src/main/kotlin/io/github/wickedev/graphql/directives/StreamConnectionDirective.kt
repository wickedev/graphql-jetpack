package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.language.BooleanValue
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull


internal const val STREAM_CONNECTION_DIRECTIVE_NAME = "stream_connection"
private val STREAM_CONNECTION_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
    
        A directive which declares that a field implements the connection spec.
    
        [Read More](https://relay.dev/docs/guided-tour/list-data/pagination/)
    """.trimIndent()


val STREAM_CONNECTION_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(STREAM_CONNECTION_DIRECTIVE_NAME)
    .description(STREAM_CONNECTION_DIRECTIVE_DESCRIPTION)
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
        it.name("initial_count")
        it.type(nonNull(Scalars.GraphQLInt))
    }
    .argument {
        it.name("if")
        it.type(Scalars.GraphQLBoolean)
        it.defaultValueLiteral(BooleanValue.of(true))
    }
    .argument {
        it.name("use_customized_batch")
        it.type(Scalars.GraphQLBoolean)
        it.defaultValueLiteral(BooleanValue.of(false))
    }
    .argument {
        it.name("dynamicKey_UNSTABLE")
        it.type(Scalars.GraphQLString)
    }
    .build()

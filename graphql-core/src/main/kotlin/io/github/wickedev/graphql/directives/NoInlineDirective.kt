package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val NO_INLINE_DIRECTIVE_NAME = "no_inline"
private val NO_INLINE_DIRECTIVE_DESCRIPTION = """
        (Relay only)

        which can be used to prevent common fragments from being inlined, resulting in smaller generated files.
    """.trimIndent()

val NO_INLINE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(NO_INLINE_DIRECTIVE_NAME)
    .description(NO_INLINE_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("raw_response_type")
        it.type(Scalars.GraphQLBoolean)
    }
    .validLocations(Introspection.DirectiveLocation.FRAGMENT_DEFINITION)
    .build()

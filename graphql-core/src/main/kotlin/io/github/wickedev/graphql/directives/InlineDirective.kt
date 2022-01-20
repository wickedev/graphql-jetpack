package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val INLINE_DIRECTIVE_NAME = "inline"
private val INLINE_DIRECTIVE_DESCRIPTION = """
        (Relay only)

        The hooks APIs that Relay exposes allow you to read data from the store only
        during the render phase. In order to read data from outside of the render
        phase (or from outside of React), Relay exposes the `@inline` directive. The
        data from a fragment annotated with `@inline` can be read using `readInlineData`.
        
        [Read More](https://relay.dev/docs/api-reference/graphql-and-directives/#inline)
    """.trimIndent()

val INLINE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(INLINE_DIRECTIVE_NAME)
    .description(INLINE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FRAGMENT_DEFINITION)
    .build()

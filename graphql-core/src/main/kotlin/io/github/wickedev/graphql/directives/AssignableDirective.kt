package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val ASSIGNABLE_DIRECTIVE_NAME = "assignable"
private val ASSIGNABLE_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
        
        Marks a given fragment as assignable.
        
        [Read More](https://fb.quip.com/4FZaADvkQPPl)
    """.trimIndent()

val ASSIGNABLE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(ASSIGNABLE_DIRECTIVE_NAME)
    .description(ASSIGNABLE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FRAGMENT_DEFINITION)
    .build()

package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val UPDATABLE_DIRECTIVE_NAME = "updatable"
private val UPDATABLE_DIRECTIVE_DESCRIPTION = """
(Relay Only)

Marks a given query or fragment as updatable.

[Read More](https://fb.quip.com/4FZaADvkQPPl)
""".trimIndent()

val UPDATABLE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(UPDATABLE_DIRECTIVE_NAME)
    .description(UPDATABLE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.QUERY, Introspection.DirectiveLocation.FRAGMENT_DEFINITION)
    .build()

package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val MATCH_DIRECTIVE_NAME = "match"
private val MATCH_DIRECTIVE_DESCRIPTION = """
    (Relay Only)

    A directive that, when used in combination with `@module`, allows users to
    download specific JS components alongside the rest of the GraphQL payload if
    the field decorated with [`@match`](https://relay.dev/docs/glossary/#match)
    has a certain type. See [3D](https://relay.dev/docs/glossary/#3d).

    [Read More](https://relay.dev/docs/glossary/#match)
""".trimIndent()

val MATCH_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(MATCH_DIRECTIVE_NAME)
    .description(MATCH_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("key")
        it.type(Scalars.GraphQLString)
    }
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .build()

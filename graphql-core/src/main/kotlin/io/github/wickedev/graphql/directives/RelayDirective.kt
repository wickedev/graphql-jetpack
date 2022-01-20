package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.language.BooleanValue
import graphql.schema.GraphQLDirective

internal const val RELAY_DIRECTIVE_NAME = "relay"
private val RELAY_DIRECTIVE_DESCRIPTION = """
    (Relay Only)

    A directive that allows you to turn off Relay's data masking.

    Read more
    [here](https://relay.dev/docs/api-reference/graphql-and-directives/#relayplural-boolean)
    and
    [here](https://relay.dev/docs/api-reference/graphql-and-directives/#relaymask-boolean).
""".trimIndent()

val RELAY_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(RELAY_DIRECTIVE_NAME)
    .description(RELAY_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("plural")
        it.type(Scalars.GraphQLBoolean)
        it.description("Marks a fragment as being backed by a GraphQLList.")
    }
    .argument {
        it.name("mask")
        it.type(Scalars.GraphQLBoolean)
        it.defaultValueLiteral(BooleanValue.of(true))
        it.description("Marks a fragment spread which should be unmasked if provided false")
    }
    .validLocations(
        Introspection.DirectiveLocation.FRAGMENT_DEFINITION,
        Introspection.DirectiveLocation.FRAGMENT_SPREAD
    )
    .build()

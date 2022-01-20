package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLNonNull.nonNull

internal const val MODULE_DIRECTIVE_NAME = "module"
private val MODULE_DIRECTIVE_DESCRIPTION = """
    (Relay Only)

    A directive that, when used in combination with
    [`@match`](https://relay.dev/docs/glossary/#match), allows users to specify
    which JS components to download if the field decorated with @match has a
    certain type. See [3D](https://relay.dev/docs/glossary/#3d).

    [Read More](https://relay.dev/docs/glossary/#module)
""".trimIndent()

val MODULE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(MODULE_DIRECTIVE_NAME)
    .description(MODULE_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("name")
        it.type(nonNull(Scalars.GraphQLString))
    }
    .validLocations(Introspection.DirectiveLocation.FRAGMENT_SPREAD)
    .build()

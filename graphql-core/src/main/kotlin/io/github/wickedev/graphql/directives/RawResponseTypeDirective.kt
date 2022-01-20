package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val RAW_RESPONSE_TYPE_DIRECTIVE_NAME = "raw_response_type"
private val RAW_RESPONSE_TYPE_DIRECTIVE_DESCRIPTION = """
    (Relay only)

    A directive added to queries which tells Relay to generate types that cover
    the `optimisticResponse` parameter to `commitMutation`.
    
    [Read More](https://relay.dev/docs/glossary/#raw_response_type)
    """.trimIndent()

val RAW_RESPONSE_TYPE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(RAW_RESPONSE_TYPE_DIRECTIVE_NAME)
    .description(RAW_RESPONSE_TYPE_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.QUERY, Introspection.DirectiveLocation.MUTATION, Introspection.DirectiveLocation.SUBSCRIPTION)
    .build()

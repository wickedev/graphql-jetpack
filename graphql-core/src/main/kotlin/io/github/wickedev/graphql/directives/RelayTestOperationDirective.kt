package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val RELAY_TEST_OPERATION_DIRECTIVE_NAME = "relay_test_operation"
private val RELAY_TEST_OPERATION_DIRECTIVE_DESCRIPTION = """
        (Relay Only)

        Will have additional metadata that will contain GraphQL type info for fields in the operation's selection
        
        [Read More](https://relay.dev/docs/guides/testing-relay-components/#relay_test_operation)
    """.trimIndent()

val RELAY_TEST_OPERATION_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(RELAY_TEST_OPERATION_DIRECTIVE_NAME)
    .description(RELAY_TEST_OPERATION_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.QUERY, Introspection.DirectiveLocation.MUTATION, Introspection.DirectiveLocation.SUBSCRIPTION)
    .build()

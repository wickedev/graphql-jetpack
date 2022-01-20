package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLNonNull.nonNull
import io.github.wickedev.graphql.enums.REQUIRED_FIELD_ACTION

internal const val REQUIRED_DIRECTIVE_NAME = "required"
private val REQUIRED_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
        
        `@required` is a directive you can add to fields in your Relay queries to
        declare how null values should be handled at runtime. You can think of it as
        saying "if this field is ever null, its parent field is invalid and should be
        null".
        
        [Read More](https://relay.dev/docs/guides/required-directive/)
    """.trimIndent()

val REQUIRED_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(REQUIRED_DIRECTIVE_NAME)
    .description(REQUIRED_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("action")
        it.type(nonNull(REQUIRED_FIELD_ACTION))
    }
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .build()

package io.github.wickedev.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLDirective

internal const val DELETE_RECORD_DIRECTIVE_NAME = "deleteRecord"
private val DELETE_RECORD_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
    
        For use within mutations. After the mutation request is complete, this field
        will be removed from the store.
    
        [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
    """.trimIndent()

val DELETE_RECORD_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(DELETE_RECORD_DIRECTIVE_NAME)
    .description(DELETE_RECORD_DIRECTIVE_DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.FIELD)
    .build()

package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.introspection.Introspection
import graphql.language.BooleanValue
import graphql.schema.GraphQLDirective

internal const val PRE_LOADABLE_DIRECTIVE_NAME = "preloadable"
private val PRE_LOADABLE_DIRECTIVE_DESCRIPTION = """
        (Relay Only)
        
        A directive that modifies queries and which causes Relay to generate
        `${'$'}Parameters.js` files and preloadable concrete requests. Required if the
        query is going to be used as part of an entry point.
        
        The `hackPreloader` argument is FB only and generates a Hack preloader file.
        
        [Read More](https://relay.dev/docs/glossary/#preloadable)
    """.trimIndent()

val PRE_LOADABLE_DIRECTIVE_TYPE: GraphQLDirective = GraphQLDirective.newDirective()
    .name(PRE_LOADABLE_DIRECTIVE_NAME)
    .description(PRE_LOADABLE_DIRECTIVE_DESCRIPTION)
    .argument {
        it.name("hackPreloader")
        it.type(Scalars.GraphQLBoolean)
        it.defaultValueLiteral(BooleanValue.of(false))
    }
    .validLocations(Introspection.DirectiveLocation.QUERY)
    .build()

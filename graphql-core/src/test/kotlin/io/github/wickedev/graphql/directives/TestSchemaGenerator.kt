package io.github.wickedev.graphql.directives

import graphql.Scalars
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema

fun generateSchemaWithDirective(directive: GraphQLDirective): GraphQLSchema {
    return GraphQLSchema.newSchema()
        .query(
            GraphQLObjectType.newObject()
                .name("Query")
                .field(
                    GraphQLFieldDefinition.newFieldDefinition()
                    .name("test")
                    .type(Scalars.GraphQLInt))
                .build()
        )
        .additionalDirectives(setOf(directive))
        .build()
}
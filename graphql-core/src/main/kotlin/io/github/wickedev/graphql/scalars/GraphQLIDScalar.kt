@file:Suppress("unused")

package io.github.wickedev.graphql.scalars

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import io.github.wickedev.graphql.extentions.toLocalID
import io.github.wickedev.graphql.extentions.typeName
import io.github.wickedev.graphql.types.ID

val GraphQLIDScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("ID")
    .description("Built-in ID")
    .coercing(object : Coercing<ID, String> {
        override fun serialize(input: Any): String {
            if (input is ID) {
                return input.encoded
            }

            throw CoercingParseValueException("Expected type 'ID' but was '${typeName(input)}'.")
        }

        override fun parseValue(input: Any): ID {
            if (input is String) {
                return input.toLocalID()
            }

            throw CoercingParseValueException("Expected type 'ID' but was '${typeName(input)}'.")
        }

        override fun parseLiteral(input: Any): ID {
            if (input is StringValue) {
                return input.value.toLocalID()
            }

            throw CoercingParseLiteralException("Expected AST type 'StringValue' but was '${typeName(input)}'.")
        }
    })
    .build()
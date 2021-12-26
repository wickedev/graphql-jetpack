package io.github.wickedev.graphql.scalars

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType

val BigIntScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("BigInt")
    .description("64-bit signed integer")
    .coercing(object : Coercing<Long?, Any?> {
        override fun serialize(dataFetcherResult: Any): Long? {
            return if (dataFetcherResult is Long) {
                dataFetcherResult
            } else {
                null
            }
        }

        override fun parseValue(input: Any): Long {
            return if (input is Long) {
                input
            } else {
                0
            }
        }

        override fun parseLiteral(input: Any): Long {
            return if (input is Long) {
                input
            } else {
                0
            }
        }
    })
    .build()
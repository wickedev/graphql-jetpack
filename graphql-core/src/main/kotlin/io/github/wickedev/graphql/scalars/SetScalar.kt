package io.github.wickedev.graphql.scalars

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType

val SetScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("SetScalar")
    .description("Collection of elements that does not support duplicate elements")
    .coercing(object : Coercing<Set<*>, List<*>> {
        override fun serialize(dataFetcherResult: Any): List<*> {
            return if (dataFetcherResult is Set<*>) {
                dataFetcherResult.toList()
            } else {
                emptyList<Any>()
            }
        }

        override fun parseValue(input: Any): Set<*> {
            return if (input is List<*>) {
                input.toSet()
            } else {
                emptySet<Any>()
            }
        }

        override fun parseLiteral(input: Any): Set<*> {
            return if (input is List<*>) {
                input.toSet()
            } else {
                emptySet<Any>()
            }
        }
    })
    .build()
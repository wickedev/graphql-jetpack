package io.github.wickedev.graphql.scalars

import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType

val SkipScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("__Skip__")
    .coercing(object : Coercing<Nothing, Nothing> {
        override fun serialize(dataFetcherResult: Any): Nothing {
            throw CoercingSerializeException("Skip types should never be used.")
        }

        override fun parseValue(input: Any): Nothing {
            throw CoercingSerializeException("Skip types should never be used.")
        }

        override fun parseLiteral(input: Any): Nothing {
            throw CoercingSerializeException("Skip types should never be used.")
        }
    })
    .build()
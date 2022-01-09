package io.github.wickedev.graphql.scalars

import graphql.language.StringValue
import graphql.schema.*
import io.r2dbc.postgresql.codec.Interval
import java.time.Duration
import java.time.format.DateTimeParseException

val INTERVAL_OBJECT_COERCING: Coercing<Interval?, Any?> = object : Coercing<Interval?, Any?> {
    private fun convertImpl(input: Any): Interval? {
        if (input is String) {
            try {
                return Interval.of(Duration.parse(input))
            } catch (ignored: DateTimeParseException) {
                // nothing to-do
            }
        } else if (input is Interval) {
            return input
        }
        return null
    }

    override fun serialize(input: Any): String {
        return if (input is Interval) {
            input.duration.toString()
        } else {
            val result = convertImpl(input) ?: throw CoercingSerializeException("Invalid value '$input' for Duration")
            result.duration.toString()
        }
    }

    override fun parseValue(input: Any): Interval {
        return convertImpl(input) ?: throw CoercingParseValueException("Invalid value '$input' for Duration")
    }

    override fun parseLiteral(input: Any): Interval {
        val value = (input as StringValue).value
        return convertImpl(value) ?: throw CoercingParseLiteralException("Invalid value '$input' for Duration")
    }
}

val PostgresIntervalScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Interval")
    .description("ISO 8601 Interval")
    .coercing(INTERVAL_OBJECT_COERCING)
    .build()
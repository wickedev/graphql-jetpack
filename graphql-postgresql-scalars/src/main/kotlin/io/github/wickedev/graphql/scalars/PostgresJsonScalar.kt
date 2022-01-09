package io.github.wickedev.graphql.scalars

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.Assert
import graphql.language.*
import graphql.scalars.util.Kit
import graphql.scalars.util.Kit.typeName
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import graphql.util.FpKit
import io.r2dbc.postgresql.codec.Json
import java.math.BigDecimal
import java.math.BigInteger
import java.util.function.Consumer

val JSON_OBJECT_COERCING: Coercing<Json?, Any?> = object : Coercing<Json?, Any?> {
    val mapper: ObjectMapper = ObjectMapper()

    override fun serialize(input: Any): Any {
        if (input is Json) {

            val json = input.asString().trim()

            val isArray = json.startsWith("[") && json.endsWith("]")

            if (isArray) {
                return mapper.readValue(json, List::class.java)
            }

            return mapper.readValue(json, Map::class.java)
        }

        throw CoercingParseValueException("Expected type 'Json' but was '${typeName(input)}'.")
    }

    override fun parseValue(input: Any): Json {
        if (input is Json) {
            return input
        }
        throw CoercingParseValueException("Expected type 'Json' but was '${typeName(input)}'.")
    }

    override fun parseLiteral(input: Any): Json {
        return parseLiteral(input, emptyMap())
    }

    override fun parseLiteral(input: Any, variables: Map<String, Any?>): Json {
        return Json.of(parseJson(input, variables).toString())
    }

    fun parseJson(input: Any?, variables: Map<String, Any?>): Any? {
        return when (input) {
            !is Value<*> -> throw CoercingParseLiteralException(
                "Expected AST type 'Value' but was '" + Kit.typeName(input) + "'."
            )
            is NullValue -> null
            is VariableReference -> variables[input.name]
            is ArrayValue -> {
                input.values
                    .map { parseLiteral(it, variables) }
            }
            is ObjectValue -> {
                val values = input.objectFields
                val parsedValues: MutableMap<String, Any?> = LinkedHashMap()
                values.forEach(
                    Consumer { fld: ObjectField ->
                        val parsedValue = parseLiteral(fld.value, variables)
                        parsedValues[fld.name] = parsedValue
                    }
                )
                parsedValues
            }
            is FloatValue -> input.value
            is StringValue -> input.value
            is IntValue -> input.value
            is BooleanValue -> input.isValue
            is EnumValue -> input.name
            else -> Assert.assertShouldNeverHappen("We have covered all Value types")
        }
    }

    override fun valueToLiteral(input: Any): Value<*> {
        if (input is String) {
            return StringValue(input)
        }
        if (input is Float) {
            return FloatValue(BigDecimal.valueOf(input.toDouble()))
        }
        if (input is Double) {
            return FloatValue(BigDecimal.valueOf(input))
        }
        if (input is BigDecimal) {
            return FloatValue(input)
        }
        if (input is BigInteger) {
            return IntValue(input)
        }
        if (input is Number) {
            return IntValue(BigInteger.valueOf(input.toLong()))
        }
        if (input is Boolean) {
            return BooleanValue(input)
        }
        if (FpKit.isIterable(input)) {
            return handleIterable(FpKit.toIterable<Any>(input))
        }
        if (input is Map<*, *>) {
            return handleMap(input)
        }
        throw UnsupportedOperationException("The ObjectScalar cant handle values of type : " + input.javaClass)
    }

    private fun handleMap(map: Map<*, *>): Value<*> {
        val builder = ObjectValue.newObjectValue()
        for ((key, value) in map) {
            if (value == null) {
                continue
            }

            val name = key.toString()
            val lit = valueToLiteral(value)
            builder.objectField(
                ObjectField.newObjectField().name(name).value(lit).build()
            )
        }
        return builder.build()
    }

    private fun handleIterable(input: Iterable<*>): Value<*> {
        val values: MutableList<Value<*>?> = ArrayList()
        for (value in input) {
            if (value == null) {
                continue
            }

            values.add(valueToLiteral(value))
        }
        return ArrayValue.newArrayValue().values(values).build()
    }
}

val PostgresJsonScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("JSON")
    .description("A JSON scalar")
    .coercing(JSON_OBJECT_COERCING)
    .build()
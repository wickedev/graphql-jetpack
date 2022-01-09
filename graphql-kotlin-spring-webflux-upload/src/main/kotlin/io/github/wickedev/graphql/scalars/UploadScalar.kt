package io.github.wickedev.graphql.scalars

import graphql.schema.*
import io.github.wickedev.graphql.types.Upload
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part

val GraphQLUploadScalar: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Upload")
    .description("A file part in a multipart request")
    .coercing(object : Coercing<Upload?, Any> {
        override fun serialize(dataFetcherResult: Any): Any {
            throw CoercingSerializeException("Upload is an input-only type")
        }

        override fun parseValue(input: Any): Upload {
            return when (input) {
                is FilePart -> Upload(input)
                else -> {
                    throw CoercingParseValueException(
                        "Expected type " +
                                Part::class.java.name +
                                " but was " +
                                input.javaClass.name
                    )
                }
            }
        }

        override fun parseLiteral(input: Any): Upload {
            throw CoercingParseLiteralException(
                "Must use variables to specify Upload values"
            )
        }
    })
    .build()
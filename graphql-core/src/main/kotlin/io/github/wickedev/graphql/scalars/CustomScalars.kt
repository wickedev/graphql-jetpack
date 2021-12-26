@file:Suppress("unused")

package io.github.wickedev.graphql.scalars

import graphql.schema.GraphQLType
import kotlin.reflect.KClassifier
import kotlin.reflect.KType

class CustomScalars private constructor(private val scalars: Map<KClassifier, GraphQLType>) {

    companion object {
        fun of(vararg scalars: Pair<KClassifier, GraphQLType>): CustomScalars {
            return CustomScalars(mapOf(*scalars))
        }
    }

    fun exists(type: KType?): Boolean = scalars.containsKey(type?.classifier)

    fun exists(type: KClassifier?): Boolean = scalars.containsKey(type)

    fun typeToGraphQLType(type: KClassifier?): GraphQLType? = scalars[type]
}

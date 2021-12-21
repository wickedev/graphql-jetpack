@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.wickedev.graphql.types

import io.github.wickedev.graphql.extentions.encodeBase64
import kotlin.reflect.KClass

data class ID(val type: String, val value: String) {

    companion object {
        val Empty = ID("", "")
    }

    val serialized: String = if (type.isNotEmpty()) "$type:$value".encodeBase64() else value.encodeBase64()

    constructor(value: String) : this("", value)

    fun toGlobalId(type: String): ID = ID(type, value)

    fun toGlobalId(type: KClass<*>): ID = ID(type.simpleName ?: "", value)

    override fun toString(): String = if (type.isEmpty()) value else "ID($type:$value:$serialized)"
}

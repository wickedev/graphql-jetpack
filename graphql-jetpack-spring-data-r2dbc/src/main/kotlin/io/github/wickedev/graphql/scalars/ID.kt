package io.github.wickedev.graphql.scalars

import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

data class ID(val type: String, val value: String) {

    companion object {
        val Empty = ID("", "")
    }

    private val encodedId: String = if (type.isNotEmpty())
        "$type:$value".encodeBase64() else value.encodeBase64()

    constructor(value: String) : this("", value)

    fun serialize(): String = encodedId

    fun toGlobalId(type: String): ID = ID(type, value)

    fun toGlobalId(type: KClass<*>): ID = ID(type.simpleName ?: "", value)

    override fun toString(): String = if (type.isEmpty()) value else "ID($type:$value:$encodedId)"
}

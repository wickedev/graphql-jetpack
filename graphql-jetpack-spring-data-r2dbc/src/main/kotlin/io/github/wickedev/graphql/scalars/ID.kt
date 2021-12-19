package io.github.wickedev.graphql.scalars

import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

data class ID(val type: String, val value: String) {

    companion object {
        val Empty = ID("", "")
    }

    constructor(value: String) : this("", value)

    fun serialize(): String = encoder.encodeToString((type + ":" + this.value).toByteArray(StandardCharsets.UTF_8))

    fun toGlobalId(type: String): ID = ID(type, value)

    fun toGlobalId(type: KClass<*>): ID = ID(type.simpleName ?: "", value)

    override fun toString(): String = if (type.isEmpty()) value else "$type:$value"
}

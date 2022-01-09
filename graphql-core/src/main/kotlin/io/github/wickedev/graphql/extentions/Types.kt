package io.github.wickedev.graphql.extentions

import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure


fun typeName(input: Any?): String? {
    return if (input == null) {
        "null"
    } else input.javaClass.simpleName
}

fun KParameter.isList(): Boolean {
    return type.isList()
}

fun KParameter.isArray(): Boolean {
    return type.isArray()
}

val KParameter.genericType: KType?
    get() = if (type.arguments.isNotEmpty()) type.arguments[0].type else null

fun KType.isList(): Boolean {
    return jvmErasure.isSubclassOf(List::class)
}

fun KType.isArray(): Boolean {
    return javaClass.isArray
}

fun Any.isCollection(): Boolean {
    return this is Array<*> || this is Collection<*> || this is Map<*, *>
}
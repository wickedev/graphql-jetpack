package io.github.wickedev.graphql.extentions

import kotlin.reflect.KClass

fun <T : Any> KClass<T>.isAssignableFrom(cls: Class<*>): Boolean {
    return java.isAssignableFrom(cls)
}

inline fun <reified R> Sequence<*>.`as`(): Sequence<R> {
    @Suppress("UNCHECKED_CAST")
    return this as Sequence<R>
}
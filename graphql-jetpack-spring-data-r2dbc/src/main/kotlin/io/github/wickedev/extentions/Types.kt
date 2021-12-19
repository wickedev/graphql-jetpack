package io.github.wickedev.extentions

import kotlin.reflect.KClass

fun <T : Any> KClass<T>.isAssignableFrom(cls: Class<*>): Boolean {
    return java.isAssignableFrom(cls)
}
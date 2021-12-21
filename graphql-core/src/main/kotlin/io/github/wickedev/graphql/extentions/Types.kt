package io.github.wickedev.graphql.extentions

fun typeName(input: Any?): String? {
    return if (input == null) {
        "null"
    } else input.javaClass.simpleName
}
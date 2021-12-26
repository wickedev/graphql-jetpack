package io.github.wickedev.graphql.spring.data.r2dbc.extentions

import java.util.*

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.lowercase(Locale.getDefault())
}
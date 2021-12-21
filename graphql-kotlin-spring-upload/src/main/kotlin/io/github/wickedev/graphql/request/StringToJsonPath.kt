package io.github.wickedev.graphql.request

fun String.toJsonPath(): String {
    return "$.${this}".replace(".(\\d+)".toRegex(), "[$1]")
}

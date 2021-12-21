package io.github.wickedev.graphql.parser

fun String.toJsonPath(): String {
    return "$.${this}".replace(".(\\d+)".toRegex(), "[$1]")
}

package io.github.wickedev.graphql.types

data class Backward(
    val last: Int? = null,
    val before: ID? = null,
) : ConnectionParam
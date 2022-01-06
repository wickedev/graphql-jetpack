package io.github.wickedev.graphql.types

data class Backward(
    val last: Int = 10,
    val before: ID? = null,
) : ConnectionParam
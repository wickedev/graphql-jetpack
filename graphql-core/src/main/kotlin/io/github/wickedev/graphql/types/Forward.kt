package io.github.wickedev.graphql.types

data class Forward(
    val first: Int = 10,
    val after: ID? = null,
) : ConnectionParam
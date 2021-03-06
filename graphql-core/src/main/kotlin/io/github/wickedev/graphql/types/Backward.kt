package io.github.wickedev.graphql.types

data class Backward(
    val last: Int? = null,
    val before: ID? = null,
    val orderBy: List<Order>? = null,
) : ConnectionParam
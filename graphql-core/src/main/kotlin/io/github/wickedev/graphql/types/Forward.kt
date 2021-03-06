package io.github.wickedev.graphql.types

data class Forward(
    val first: Int? = null,
    val after: ID? = null,
    val orderBy: List<Order>? = null,
) : ConnectionParam
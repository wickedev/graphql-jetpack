package io.github.wickedev.graphql.types

data class PageInfo(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val startCursor: String,
    val endCursor: String,
)
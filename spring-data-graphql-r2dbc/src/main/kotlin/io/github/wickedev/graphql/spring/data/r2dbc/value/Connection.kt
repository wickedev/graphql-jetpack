package io.github.wickedev.graphql.spring.data.r2dbc.value

import io.github.wickedev.graphql.interfaces.Node

data class Connection<T: Node>(
    val edges: List<Edge<T>>,
    val pageInfo: PageInfo
)

data class Edge<T: Node>(
    val node: T,
    val cursor: ConnectionCursor
)

data class ConnectionCursor(
    val value: String
)

data class PageInfo(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val startCursor: String,
    val endCursor: String,
)
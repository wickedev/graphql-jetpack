package io.github.wickedev.graphql.types

import io.github.wickedev.graphql.interfases.Node

data class Connection<T: Node>(
    val edges: List<Edge<T>>,
    val pageInfo: PageInfo
)

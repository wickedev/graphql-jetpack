package io.github.wickedev.graphql.types

import io.github.wickedev.graphql.interfases.Node

data class Edge<T: Node>(
    val node: T,
    val cursor: ID
)
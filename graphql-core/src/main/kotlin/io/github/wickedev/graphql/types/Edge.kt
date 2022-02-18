package io.github.wickedev.graphql.types

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.unions.Response

data class Edge<T: Node>(
    val node: T,
    val cursor: ID
): Response
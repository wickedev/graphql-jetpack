package io.github.wickedev.graphql.types

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.unions.Response

data class Connection<T: Node>(
    val edges: List<Edge<T>>,
    val pageInfo: PageInfo
): Response

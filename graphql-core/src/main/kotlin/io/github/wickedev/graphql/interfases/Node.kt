package io.github.wickedev.graphql.interfases

import io.github.wickedev.graphql.types.ID
import io.github.wickedev.graphql.unions.Response

interface Node : Response {
    val id: ID
}
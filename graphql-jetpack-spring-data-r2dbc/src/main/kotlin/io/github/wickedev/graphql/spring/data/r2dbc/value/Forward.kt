package io.github.wickedev.graphql.spring.data.r2dbc.value

import io.github.wickedev.graphql.scalars.ID

data class Forward(
    val first: Int?,
    val after: ID?,
)